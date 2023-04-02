package fi.tuni.resourcedescription.service.security;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceExistsException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.payload.request.LoginRequest;
import fi.tuni.resourcedescription.payload.request.RefreshTokenRequest;
import fi.tuni.resourcedescription.payload.request.SignupRequest;
import fi.tuni.resourcedescription.payload.response.JwtResponse;
import fi.tuni.resourcedescription.payload.response.RefreshTokenResponse;
import java.security.Principal;
import org.springframework.lang.Nullable;

public interface AuthService {
  User registerUser(SignupRequest signupRequest)
    throws ResourceExistsException, InternalErrorException, ResourceNotFoundException;
  @Nullable
  User getUserFromPrinciple(Principal principal);
  JwtResponse authenticateUser(LoginRequest loginRequest) throws InternalErrorException, ResourceNotFoundException;
  RefreshTokenResponse renewRefreshToken(RefreshTokenRequest refreshTokenRequest) throws InternalErrorException, ResourceNotFoundException;
}
