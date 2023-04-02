package fi.tuni.resourcedescription.payload.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class JwtResponse {
  private String accessToken;
  private final String type = "Bearer";
  private String refreshToken;
  private Long id;
  private String username;
  private String email;
  private List<String> roles;

  public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, List<String> roles) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }
}
