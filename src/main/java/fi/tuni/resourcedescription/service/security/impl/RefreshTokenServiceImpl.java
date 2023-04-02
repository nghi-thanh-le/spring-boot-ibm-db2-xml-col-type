package fi.tuni.resourcedescription.service.security.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.RefreshToken;
import fi.tuni.resourcedescription.repository.security.RefreshTokenRepository;
import fi.tuni.resourcedescription.service.security.RefreshTokenService;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private static final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

  @Value("${rdtuni.app.jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

  private final RefreshTokenRepository refreshTokenRepository;

  @Autowired
  public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public RefreshToken findByToken(String token) throws ResourceNotFoundException {
    return refreshTokenRepository.findByToken(token)
      .orElseThrow(() -> new ResourceNotFoundException("Token does not exist in DB!."));
  }

  @Override
  public RefreshToken getTokenByUserId(Integer userId) throws InternalErrorException {
    if (Objects.isNull(userId)) {
      throw new InternalErrorException("Invalid userId to get a refresh token!");
    }

    return refreshTokenRepository.getByUserId(userId);
  }

  @Override
  public RefreshToken createTokenByUserId(Integer userId) throws InternalErrorException {
    if (Objects.isNull(userId)) {
      throw new InternalErrorException("Invalid userId to generate a refresh token!");
    }

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUserId(userId);
    refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpirationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    try {
      return refreshTokenRepository.save(refreshToken);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteTokenByUserId(Integer userId) throws InternalErrorException {
    if (Objects.isNull(userId)) {
      throw new InternalErrorException("Invalid userId to generate a delete the token!");
    }

    try {
      refreshTokenRepository.deleteByUserId(userId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteTokenById(Integer tokenId) throws InternalErrorException {
    if (Objects.isNull(tokenId)) {
      throw new InternalErrorException("Cannot delete a token with null id!");
    }

    try {
      refreshTokenRepository.deleteById(tokenId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public boolean isTokenExpired(RefreshToken token) throws InternalErrorException {
    if (Objects.isNull(token) || Objects.isNull(token.getExpiryDate())) {
      throw new InternalErrorException("Cannot validate a null token");
    }

    return token.getExpiryDate().compareTo(Instant.now()) < 0;
  }

  @Override
  public boolean isTokenWithUserIdExist(Integer userId) {
    if (Objects.isNull(userId)) {
      log.warn("Invalid userId to generate a refresh token!");
      return false;
    }
    return refreshTokenRepository.existsByUserId(userId);
  }
}
