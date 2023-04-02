package fi.tuni.resourcedescription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends Exception {
  public InternalErrorException(String message) {
    super(message);
  }
}
