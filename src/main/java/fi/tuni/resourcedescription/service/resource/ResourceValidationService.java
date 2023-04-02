package fi.tuni.resourcedescription.service.resource;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;

import java.io.IOException;

public interface ResourceValidationService {
  ResourceValidationResult validateRdById(String rdId) throws IOException, ResourceNotFoundException,
    InternalErrorException;
}
