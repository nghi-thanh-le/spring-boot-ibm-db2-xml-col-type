package fi.tuni.resourcedescription.service.security.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceExistsException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.RefreshToken;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.payload.SimpleUserDetails;
import fi.tuni.resourcedescription.payload.request.LoginRequest;
import fi.tuni.resourcedescription.payload.request.RefreshTokenRequest;
import fi.tuni.resourcedescription.payload.request.SignupRequest;
import fi.tuni.resourcedescription.payload.response.JwtResponse;
import fi.tuni.resourcedescription.payload.response.RefreshTokenResponse;
import fi.tuni.resourcedescription.service.security.AuthService;
import fi.tuni.resourcedescription.service.security.JwtUtils;
import fi.tuni.resourcedescription.service.security.RefreshTokenService;
import fi.tuni.resourcedescription.service.user.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final JwtUtils jwtUtils;

  @Autowired
  public AuthServiceImpl(UserService userService,
                         PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         RefreshTokenService refreshTokenService,
                         JwtUtils jwtUtils) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.refreshTokenService = refreshTokenService;
    this.jwtUtils = jwtUtils;
  }

  @Override
  public User registerUser(SignupRequest signupRequest)
    throws ResourceExistsException, InternalErrorException, ResourceNotFoundException {
    if (userService.existsByUsername(signupRequest.getUsername())) {
      throw new ResourceExistsException("Error: Username is already taken!");
    }
    if (userService.existsByEmail(signupRequest.getEmail())) {
      throw new ResourceExistsException("Error: Email is already in use!");
    }

    // Create new user's account
    User user = new User();
    user.setUsername(signupRequest.getUsername());
    user.setEmail(signupRequest.getEmail());
    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
    // TODO: should the company always be TUNI?
    user.setCompany(userService.getCompanyById(1));
    user.getRoles().addAll(signupRequest.getRoles());

    try {
      return userService.addNewUser(user);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Nullable
  public User getUserFromPrinciple(Principal principal) {
    return Optional.ofNullable(principal)
      .map(Principal::getName)
      .map(userName -> {
        try {
          return userService.getUserByUsername(userName);
        } catch (ResourceNotFoundException | InternalErrorException e) {
          log.error(e.getMessage(), e);
          return null;
        }
      })
      .orElse(null);
  }

  @Override
  public JwtResponse authenticateUser(LoginRequest loginRequest) throws InternalErrorException {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());

    RefreshToken refreshToken = initRefreshToken(userDetails);
    return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
  }

  @Override
  public RefreshTokenResponse renewRefreshToken(RefreshTokenRequest refreshTokenRequest)
    throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(refreshTokenRequest) || StringUtils.isBlank(refreshTokenRequest.getRefreshToken())) {
      throw new InternalErrorException("Invalid refresh token refresh. Probably trying to passing a null object.");
    }

    RefreshToken tokenFromDb = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());
    Integer userId = tokenFromDb.getUserId();

    if (refreshTokenService.isTokenExpired(tokenFromDb)) {
      log.info("Renew expired token.");
      refreshTokenService.deleteTokenById(tokenFromDb.getId());
      tokenFromDb = refreshTokenService.createTokenByUserId(userId);
    }

    User userFromDb = userService.getUserById(userId);
    String jwt = jwtUtils.generateJwtToken(userFromDb.getUsername());

    return new RefreshTokenResponse(jwt, tokenFromDb.getToken());
  }

  private RefreshToken initRefreshToken(SimpleUserDetails userDetails) throws InternalErrorException {
    RefreshToken refreshToken;
    if (refreshTokenService.isTokenWithUserIdExist(userDetails.getId().intValue())) {
      refreshToken = refreshTokenService.getTokenByUserId(userDetails.getId().intValue());
      if (refreshTokenService.isTokenExpired(refreshToken)) {
        refreshTokenService.deleteTokenByUserId(userDetails.getId().intValue());
        refreshToken = refreshTokenService.createTokenByUserId(userDetails.getId().intValue());
      }
    } else {
      refreshToken = refreshTokenService.createTokenByUserId(userDetails.getId().intValue());
    }
    return refreshToken;
  }
}
