package fi.tuni.resourcedescription.service.resource.impl;

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
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceDescriptionRepository;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceReviewRepository;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceStageRepository;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceSupplementRepository;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceValidationResultRepository;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import java.sql.Blob;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceServiceImpl implements ResourceService {
  private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);

  private final ResourceStageRepository resourceStageRepository;
  private final ResourceDescriptionRepository resourceDescriptionRepository;
  private final ResourceSupplementRepository resourceSupplementRepository;
  private final ResourceReviewRepository resourceReviewRepository;
  private final ResourceValidationResultRepository resourceValidationResultRepository;
  private final ByteArrayTransformationService xmlBytesTransformService;

  @Autowired
  public ResourceServiceImpl(ResourceDescriptionRepository resourceDescriptionRepository,
                             ResourceSupplementRepository resourceSupplementRepository,
                             ResourceStageRepository resourceStageRepository,
                             ResourceReviewRepository resourceReviewRepository,
                             ResourceValidationResultRepository resourceValidationResultRepository,
                             ByteArrayTransformationService xmlBytesTransformService) {
    this.resourceDescriptionRepository = resourceDescriptionRepository;
    this.resourceSupplementRepository = resourceSupplementRepository;
    this.resourceStageRepository = resourceStageRepository;
    this.resourceReviewRepository = resourceReviewRepository;
    this.resourceValidationResultRepository = resourceValidationResultRepository;
    this.xmlBytesTransformService = xmlBytesTransformService;
  }

  @Override
  public List<ResourceDescription> getRDs() {
    return resourceDescriptionRepository.findAll();
  }

  @Override
  public ResourceDescription getRDById(String id) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid resource id : " + id + ".");
    }

    return resourceDescriptionRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("RD with id " + id + " does not exist."));
  }

  @Override
  public String getFullRDById(String id) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid id " + id + ".");
    }

    return xmlBytesTransformService.transformBytesArray(getRDById(id).getContent(), false);
  }

  @Override
  public void deleteRdById(String rdId)
    throws InternalErrorException, ResourceNotFoundException, ResourceStillHasBindingSupplementaryFilesException {
    if (StringUtils.isBlank(rdId)) {
      throw new InternalErrorException("Invalid ID to find supplementary files");
    }

    List<ResourceSupplementaryFile> rdSupFiles = getRDSupplementaryFilesById(rdId);
    if (!rdSupFiles.isEmpty()) {
      throw new ResourceStillHasBindingSupplementaryFilesException("Cannot delete resource with id " + rdId + " as it still has binding supplementary files in DB.");
    }

    try {
      resourceDescriptionRepository.deleteById(rdId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public ResourceDescription uploadXmlFile(User user, String fileName, MultipartFile file)
    throws InternalErrorException, ResourceExistsException {
    if (StringUtils.isBlank(fileName)) {
      throw new InternalErrorException("Cannot save a resource file with null name.");
    }

    if (Objects.isNull(file)) {
      throw new InternalErrorException("Invalid file to save.");
    }

    if (Objects.isNull(user)) {
      throw new InternalErrorException("An resource file must be bound to a user. Cannot provide a null user.");
    }

    if (resourceDescriptionRepository.existsById(fileName)) {
      throw new ResourceExistsException("Resource " + file.getOriginalFilename() + " already exists.");
    }

    try {
      ResourceDescription rdFile = new ResourceDescription();
      rdFile.setId(fileName);
      rdFile.setFileName(file.getOriginalFilename());
      rdFile.setContent(file.getBytes());
      rdFile.setUserId(user.getId());
      rdFile.setStageId(ResourceStageConstant.UPLOAD.getIdVal());

      return resourceDescriptionRepository.save(rdFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public ResourceDescription replaceXmlFileById(User user, String rdId, MultipartFile file)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(rdId) || Objects.isNull(file)) {
      throw new InternalErrorException("Invalid ID or File to replace");
    }

    if (Objects.isNull(user)) {
      throw new InternalErrorException("An resource file must be bound to a user. Cannot provide a null user.");
    }

    ResourceDescription rdFile = getRDById(rdId);

    if ((Objects.isNull(rdFile.getUserId()) || Objects.isNull(user.getId())) ||
        !user.getId().equals(rdFile.getUserId())) {
      throw new InternalErrorException("User id " + user.getId() + " does not own this rdFile " + rdFile.getId());
    }

    try {
      rdFile.setContent(file.getBytes());
      rdFile.setStageId(ResourceStageConstant.UPLOAD.getIdVal());
      return resourceDescriptionRepository.save(rdFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void changeRdStageById(String rdId, ResourceStageConstant stageId)
    throws InternalErrorException, ResourceNotFoundException {
    changeRdStageById(rdId, stageId.getIdVal());
  }

  @Override
  @Transactional
  public void changeRdStageById(String rdId, String stageId) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(rdId) || StringUtils.isBlank(stageId)) {
      throw new InternalErrorException("Invalid resource ID or invalid stageId");
    }

    String newStageId = stageId.toUpperCase();

    if (!resourceDescriptionRepository.existsById(rdId)) {
      throw new ResourceNotFoundException("Resource with id " + rdId + " does not exists!");
    }

    if (!resourceStageRepository.existsById(newStageId)) {
      throw new ResourceNotFoundException("Resource Stage with id " + stageId + " does not exists!");
    }

    // TODO: there's probably some rules like not allowed to approve a REVIEW_RETURNED stage.

    try {
      resourceDescriptionRepository.setRdStage(newStageId, rdId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }

  }

  @Override
  @Transactional
  public void reviewRdById(String rdId, ResourceReviewDTO rdReviewRequest) throws InternalErrorException {
    if (StringUtils.isEmpty(rdId)) {
      throw new InternalErrorException("Invalid resource id " + rdId);
    }

    if (Objects.isNull(rdReviewRequest)) {
      throw new InternalErrorException("Cannot save a null review request!");
    }

    ResourceReview resourceReview = new ResourceReview();
    resourceReview.setReview(rdReviewRequest.getReview());
    resourceReview.setUserId(rdReviewRequest.getUserId());
    resourceReview.setId(rdId);

    try {
      resourceReviewRepository.save(resourceReview);

      if (StringUtils.isNoneBlank(rdReviewRequest.getStageId())) {
        changeRdStageById(rdId, rdReviewRequest.getStageId());
      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateRDReviewById(String rdId, ResourceReviewDTO rdReview)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isEmpty(rdId)) {
      throw new InternalErrorException("Invalid resource id " + rdId);
    }

    if (Objects.isNull(rdReview)) {
      throw new InternalErrorException("Cannot update save a null review request!");
    }

    ResourceReview resourceReview = resourceReviewRepository.findById(rdId)
      .orElseThrow(() -> new ResourceNotFoundException("RD Review with id " + rdId + " does not exist."));

    resourceReview.setReview(rdReview.getReview());
    resourceReview.setUserId(rdReview.getUserId());
    resourceReview.setReviewAt(Timestamp.from(Instant.now()));

    try {
      resourceReviewRepository.save(resourceReview);

      if (StringUtils.isNoneBlank(rdReview.getStageId())) {
        changeRdStageById(rdId, rdReview.getStageId());
      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public void deleteRDReviewById(String rdId) throws InternalErrorException {
    if (StringUtils.isEmpty(rdId)) {
      throw new InternalErrorException("Invalid resource id " + rdId);
    }

    try {
      resourceReviewRepository.deleteById(rdId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<ResourceSupplementaryFile> getRDSupplementaryFilesById(String resourceId)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(resourceId)) {
      throw new InternalErrorException("Invalid ID to find supplementary files");
    }

    ResourceDescription resourceDescription = getRDById(resourceId);
    return resourceSupplementRepository.findAllByResourceId(resourceDescription.getId())
      .orElseThrow(() -> new ResourceNotFoundException("RD Supplementary files of RD id " + resourceId + " does not exist."));
  }

  @Override
  public ResourceSupplementaryFile uploadSupplementaryFileToRDById(String resourceId, MultipartFile file)
    throws InternalErrorException, ResourceNotFoundException, ResourceExceedsMaxSizeException {
    if (StringUtils.isBlank(resourceId)) {
      throw new InternalErrorException("Invalid ID to find supplementary files");
    }

    if (!resourceDescriptionRepository.existsById(resourceId)) {
      throw new ResourceNotFoundException("Resource file with id " + resourceId + " does not exist!");
    }

    if (Objects.isNull(file)) {
      throw new InternalErrorException("Invalid file to upload");
    }

    if (fileIsLargerThan100GB(file)) {
      throw new ResourceExceedsMaxSizeException("File is too large.");
    }

    try {
      Blob myBlob = new SerialBlob(file.getBytes());
      ResourceSupplementaryFile supplementFile = new ResourceSupplementaryFile();
      supplementFile.setFileName(file.getOriginalFilename());
      supplementFile.setContent(myBlob);
      supplementFile.setResourceId(resourceId);
      return resourceSupplementRepository.save(supplementFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public ResourceSupplementaryFile getSupplementaryFileById(String resourceId, Integer fileId)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(resourceId) || Objects.isNull(fileId)) {
      throw new InternalErrorException("Invalid id or fileId to find supplementary files");
    }

    return resourceSupplementRepository
      .findByIdAndResourceId(fileId, resourceId)
      .orElseThrow(() -> new ResourceNotFoundException("Supplementary file with id " + resourceId + " does not exist or fileId " + fileId + " does not exist ."));
  }

  @Override
  public ResourceSupplementaryFile updateRDSupplementaryFileById(String resourceId, Integer fileId, MultipartFile file)
    throws InternalErrorException, ResourceExceedsMaxSizeException, ResourceNotFoundException {
    if (StringUtils.isEmpty(resourceId) || Objects.isNull(fileId)) {
      throw new InternalErrorException("Invalid id or fileId to find supplementary files");
    }

    if (Objects.isNull(file)) {
      throw new InternalErrorException("Null file to replace.");
    }

    if (fileIsLargerThan100GB(file)) {
      throw new ResourceExceedsMaxSizeException("File is too large.");
    }

    ResourceSupplementaryFile resourceSupplementFile = getSupplementaryFileById(resourceId, fileId);

    if (!resourceSupplementFile.getFileName().equals(file.getOriginalFilename())) {
      throw new InternalErrorException("Uploaded file has different name to file to be updated");
    }

    try {
      Blob myBlob = new SerialBlob(file.getBytes());
      resourceSupplementFile.setContent(myBlob);
      resourceSupplementFile.setFileName(file.getOriginalFilename());
      return resourceSupplementRepository.save(resourceSupplementFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteRDSupplementaryFileById(String resourceId, Integer fileId) throws InternalErrorException {
    if (StringUtils.isBlank(resourceId) || Objects.isNull(fileId)) {
      throw new InternalErrorException("Invalid id or fileId to delete the supplementary file");
    }

    try {
      resourceSupplementRepository.deleteByIdAndResourceId(fileId, resourceId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public ResourceValidationResult getRDValidationReportById(String id) throws InternalErrorException, ResourceNotFoundException {
    return generalFindByIdFromRepository(id, resourceValidationResultRepository);
  }

  @Override
  public List<ResourceReview> getAllReviews() {
    return resourceReviewRepository.findAll();
  }

  @Override
  public ResourceReview getRDReviewById(String id) throws InternalErrorException, ResourceNotFoundException {
    return generalFindByIdFromRepository(id, resourceReviewRepository);
  }

  private boolean fileIsLargerThan100GB(MultipartFile file) {
    return ((file.getSize() / 1024) / 1024) > 100;
  }

  private <T, H extends JpaRepository<T, String>> T generalFindByIdFromRepository(String id, H repository) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid resource id");
    }

    return repository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " does not exist."));
  }
}
