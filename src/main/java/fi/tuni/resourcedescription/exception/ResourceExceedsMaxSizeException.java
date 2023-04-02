package fi.tuni.resourcedescription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
public class ResourceExceedsMaxSizeException extends Exception {
  public ResourceExceedsMaxSizeException(String message) {
    super(message);
  }
}
