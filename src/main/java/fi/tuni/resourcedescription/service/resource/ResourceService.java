package fi.tuni.resourcedescription.service.resource;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceExceedsMaxSizeException;
import fi.tuni.resourcedescription.exception.ResourceExistsException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.exception.ResourceStillHasBindingSupplementaryFilesException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceReview;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceSupplementaryFile;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.payload.request.ResourceReviewDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
  // RDs
  List<ResourceDescription> getRDs();
  ResourceDescription getRDById(String id) throws InternalErrorException, ResourceNotFoundException;
  String getFullRDById(String id) throws InternalErrorException, ResourceNotFoundException;
  void deleteRdById(String rdId)
    throws InternalErrorException, ResourceNotFoundException, ResourceStillHasBindingSupplementaryFilesException;
  ResourceDescription uploadXmlFile(User user, String fileName, MultipartFile file)
    throws InternalErrorException, ResourceExistsException;
  ResourceDescription replaceXmlFileById(User user, String rdId, MultipartFile file)
    throws InternalErrorException, ResourceNotFoundException;
  void changeRdStageById(String rdId, ResourceStageConstant stageId)
    throws InternalErrorException, ResourceNotFoundException;
  void changeRdStageById(String rdId, String stageId) throws InternalErrorException, ResourceNotFoundException;
  void reviewRdById(String rdId, ResourceReviewDTO rdReviewRequest) throws InternalErrorException;
  void updateRDReviewById(String rdId, ResourceReviewDTO rdReview)
    throws InternalErrorException, ResourceNotFoundException;
  void deleteRDReviewById(String rdId) throws InternalErrorException;

  // RD Supplementary Files
  List<ResourceSupplementaryFile> getRDSupplementaryFilesById(String resourceId)
    throws InternalErrorException, ResourceNotFoundException;
  ResourceSupplementaryFile uploadSupplementaryFileToRDById(String resourceId, MultipartFile file)
    throws InternalErrorException, ResourceNotFoundException, ResourceExceedsMaxSizeException;
  ResourceSupplementaryFile getSupplementaryFileById(String resourceId, Integer fileId)
    throws InternalErrorException, ResourceNotFoundException;
  ResourceSupplementaryFile updateRDSupplementaryFileById(String resourceId, Integer fileId, MultipartFile file)
    throws InternalErrorException, ResourceExceedsMaxSizeException, ResourceNotFoundException;
  void deleteRDSupplementaryFileById(String resourceId, Integer fileId) throws InternalErrorException;

  // RD Validation
  ResourceValidationResult getRDValidationReportById(String id) throws InternalErrorException, ResourceNotFoundException;

  // RD Review
  List<ResourceReview> getAllReviews();
  ResourceReview getRDReviewById(String id) throws InternalErrorException, ResourceNotFoundException;
}
