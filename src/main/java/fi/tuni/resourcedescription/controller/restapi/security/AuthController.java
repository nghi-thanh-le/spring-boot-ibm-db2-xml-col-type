package fi.tuni.resourcedescription.controller.restapi.security;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.payload.request.LoginRequest;
import fi.tuni.resourcedescription.payload.request.RefreshTokenRequest;
import fi.tuni.resourcedescription.payload.request.SignupRequest;
import fi.tuni.resourcedescription.payload.response.JwtResponse;
import fi.tuni.resourcedescription.payload.response.RefreshTokenResponse;
import fi.tuni.resourcedescription.service.security.AuthService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping(
    value = "signin",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(authService.authenticateUser(loginRequest));
  }

  @PostMapping(
    value = "refreshtoken",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(authService.renewRefreshToken(refreshTokenRequest));
  }

  @PostMapping(
    value = "signup",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<User> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
    throws Exception {
    return ResponseEntity.ok(authService.registerUser(signUpRequest));
  }
}
