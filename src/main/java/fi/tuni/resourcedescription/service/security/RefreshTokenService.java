package fi.tuni.resourcedescription.service.security;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.RefreshToken;
import javax.transaction.Transactional;

public interface RefreshTokenService {
    RefreshToken findByToken(String token) throws ResourceNotFoundException;
    RefreshToken getTokenByUserId(Integer userId) throws InternalErrorException;
    RefreshToken createTokenByUserId(Integer userId) throws InternalErrorException;

    @Transactional
    void deleteTokenByUserId(Integer userId) throws InternalErrorException;
    @Transactional
    void deleteTokenById(Integer tokenId) throws InternalErrorException;

    boolean isTokenExpired(RefreshToken token) throws InternalErrorException;
    boolean isTokenWithUserIdExist(Integer userId);
}
