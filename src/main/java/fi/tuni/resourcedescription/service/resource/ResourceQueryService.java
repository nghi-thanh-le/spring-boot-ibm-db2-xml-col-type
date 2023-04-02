package fi.tuni.resourcedescription.service.resource;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.payload.QueryResult;
import java.util.List;

public interface ResourceQueryService {
  // Queries
  String getSimplifiedRDById(String id) throws InternalErrorException, ResourceNotFoundException;
  List<ResourceDescription> getRDsImplementingStdByStdId(String stdId) throws InternalErrorException;
  List<ResourceDescription> getRDsImplementingCapabilityByName(String capaName) throws InternalErrorException;
  QueryResult getRdAllNodesByName(String id, String name) throws InternalErrorException, ResourceNotFoundException;
  QueryResult getAllEClasses() throws InternalErrorException;
  QueryResult getEClassesById(String id) throws InternalErrorException;
  List<ResourceDescription> getRDsImplementingEClassesById(String id) throws InternalErrorException;
  QueryResult getStdsOfRDByIdAndStdVal(String id, String stdVal)
    throws InternalErrorException, ResourceNotFoundException;
  QueryResult getStdsOfRDById(String id) throws InternalErrorException, ResourceNotFoundException;
  List<String> getAllCategories() throws InternalErrorException;
  List<ResourceDescription> getRdsByCategoryName(String name) throws InternalErrorException;
  List<ResourceDescription> getRdsByVendorName(String name) throws InternalErrorException;
}
