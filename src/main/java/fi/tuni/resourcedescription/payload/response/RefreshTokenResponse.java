package fi.tuni.resourcedescription.payload.response;

import lombok.Data;

@Data
public class RefreshTokenResponse {
  private String accessToken;
  private String refreshToken;
  private final String type = "Bearer";

  public RefreshTokenResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
