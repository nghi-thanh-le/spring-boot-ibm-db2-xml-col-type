package fi.tuni.resourcedescription.exception;

import fi.tuni.resourcedescription.payload.response.MessageResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(
    value = ResourceNotFoundException.class
  )
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public MessageResponse resourceNotFoundException(ResourceNotFoundException ex) {
    return new MessageResponse(
      HttpStatus.NOT_FOUND.value(),
      LocalDateTime.now(),
      ex.getMessage(),
      "ResourceNotFound");
  }

  @ExceptionHandler(
    value = {
      InternalErrorException.class,
      ResourceStillHasBindingSupplementaryFilesException.class
    }
  )
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public MessageResponse generalInternalErrorException(Exception ex) {
    return new MessageResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      LocalDateTime.now(),
      ex.getMessage(),
      "InternalError");
  }

  @ExceptionHandler(
    value = ResourceExceedsMaxSizeException.class
  )
  @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
  public MessageResponse postedTooLargeDataException(ResourceExceedsMaxSizeException ex) {
    return new MessageResponse(
      HttpStatus.PAYLOAD_TOO_LARGE.value(),
      LocalDateTime.now(),
      ex.getMessage(),
      "PayloadTooLarge");
  }

  @ExceptionHandler(
    value = ResourceExistsException.class
  )
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public MessageResponse tryingToUploadExistedResource(ResourceExistsException ex) {
    return new MessageResponse(
      HttpStatus.CONFLICT.value(),
      LocalDateTime.now(),
      ex.getMessage(),
      "Conflict");
  }
}
