package fi.tuni.resourcedescription.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
  private static final Logger log = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    final String expiredErrorMessage = (String) request.getAttribute("expiredErrorMessage");
    log.error("Unauthorized error: {}", authException.getMessage());
    log.error("expiredErrorMessage: {}", expiredErrorMessage);
    if (StringUtils.isBlank(expiredErrorMessage)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    } else {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, expiredErrorMessage);
    }
  }
}
