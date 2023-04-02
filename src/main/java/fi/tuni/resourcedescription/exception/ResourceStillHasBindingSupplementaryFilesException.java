package fi.tuni.resourcedescription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class ResourceStillHasBindingSupplementaryFilesException extends Exception {

  public ResourceStillHasBindingSupplementaryFilesException(String message) {
    super(message);
  }

}
