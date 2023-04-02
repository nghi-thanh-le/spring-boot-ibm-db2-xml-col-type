package fi.tuni.resourcedescription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceExistsException extends Exception {
  public ResourceExistsException(String message) {
    super(message);
  }
}
