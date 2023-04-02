package fi.tuni.resourcedescription.service.resource;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.capabilitiy.Capability;
import fi.tuni.resourcedescription.model.rcp.standard.Standard;
import fi.tuni.resourcedescription.payload.request.standard.StandardDTO;
import java.util.List;

public interface ResourceCatalogueService {
  // Standards
  List<Standard> getStandards();
  Standard getStandardById(Integer id) throws ResourceNotFoundException;
  Standard addNewStandard(StandardDTO standard) throws InternalErrorException;
  Standard updateStandardById(Integer id, StandardDTO standard) throws InternalErrorException, ResourceNotFoundException;
  boolean deleteStandardById(Integer id) throws InternalErrorException, ResourceNotFoundException;
  Long getTotalNumberOfStandards();

  // Capabilities
  List<Capability> getCapabilities();
  List<Capability> getCapabilitiesByName(String name) throws InternalErrorException;
  Capability getCapabilityByName(String name) throws ResourceNotFoundException;
}
