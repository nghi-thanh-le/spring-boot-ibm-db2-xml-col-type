package fi.tuni.resourcedescription.service.resource.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {
  private static final String SAMPLE_RD_ID = "sample_id";
  private static final Integer SAMPLE_RD_SUPPLEMENTARY_FILE_ID = 1234;
  private static final String NON_EXISTENCE_RD_ID = "non-existence-rd-id";
  private static final String SAMPLE_RD_CONTENT = "<sample>foo</sample>";
  private static final String SAMPLE_FILE_NAME = "sample_file_name";

  @Mock
  private ResourceStageRepository resourceStageRepository;
  @Mock
  private ResourceDescriptionRepository resourceDescriptionRepository;
  @Mock
  private ResourceSupplementRepository resourceSupplementRepository;
  @Mock
  private ResourceReviewRepository resourceReviewRepository;
  @Mock
  private ResourceValidationResultRepository resourceValidationResultRepository;
  @Mock
  private ByteArrayTransformationService xmlBytesTransformService;

  @InjectMocks
  private ResourceServiceImpl resourceService;

  @Test
  public void test_get_rds_must_be_from_repository() {
    resourceService.getRDs();

    verify(resourceDescriptionRepository).findAll();
  }

  @Test
  public void test_get_rd_by_id_with_invalid_parameter() {
    assertThrows(InternalErrorException.class, () -> resourceService.getRDById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.getRDById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getRDById(NON_EXISTENCE_RD_ID));
  }

  @Test
  public void test_get_rd_by_valid_id() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription sampleRD = initSampleRDFromDB();
    when(sampleRD.getId()).thenReturn(SAMPLE_RD_ID);


    ResourceDescription foundRD = resourceService.getRDById(SAMPLE_RD_ID);

    assertEquals(foundRD.getId(), sampleRD.getId());
  }

  @Test
  public void test_get_full_rd_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> resourceService.getFullRDById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.getFullRDById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getFullRDById(NON_EXISTENCE_RD_ID));
  }

  @Test
  public void test_get_full_rd_by_id_must_be_transformed() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription sampleRD = initSampleRDFromDB();
    when(sampleRD.getContent()).thenReturn(SAMPLE_RD_CONTENT.getBytes());

    resourceService.getFullRDById(SAMPLE_RD_ID);

    verify(xmlBytesTransformService).transformBytesArray(sampleRD.getContent(), false);
  }

  @Test
  public void test_delete_rd_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRdById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRdById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.deleteRdById(NON_EXISTENCE_RD_ID));
  }

  @Test
  public void test_delete_rd_by_id_with_valid_params_but_file_still_has_supplementary_files() {
    ResourceDescription sampleRD = initSampleRDFromDB();

    when(resourceSupplementRepository.findAllByResourceId(sampleRD.getId()))
      .thenReturn(Optional.of(List.of(mock(ResourceSupplementaryFile.class))));

    assertThrows(ResourceStillHasBindingSupplementaryFilesException.class, () -> resourceService.deleteRdById(
      SAMPLE_RD_ID));
  }

  @Test
  public void test_delete_rd_by_id_with_valid_params()
    throws InternalErrorException, ResourceNotFoundException, ResourceStillHasBindingSupplementaryFilesException {
    ResourceDescription sampleRD = initSampleRDFromDB();

    when(resourceSupplementRepository.findAllByResourceId(sampleRD.getId()))
      .thenReturn(Optional.of(Collections.emptyList()));

    resourceService.deleteRdById(SAMPLE_RD_ID);
    verify(resourceDescriptionRepository).deleteById(SAMPLE_RD_ID);
  }

  @Test
  public void test_upload_rd_file_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.uploadXmlFile(null, SAMPLE_FILE_NAME, mock(MultipartFile.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.uploadXmlFile(mock(User.class), null, mock((MultipartFile.class))));
    assertThrows(InternalErrorException.class, () -> resourceService.uploadXmlFile(mock(User.class), StringUtils.EMPTY, mock((MultipartFile.class))));
    assertThrows(InternalErrorException.class, () -> resourceService.uploadXmlFile(mock(User.class), SAMPLE_FILE_NAME, null));

    when(resourceDescriptionRepository.existsById(anyString())).thenReturn(true);
    assertThrows(
      ResourceExistsException.class, () -> resourceService.uploadXmlFile(mock(User.class), SAMPLE_FILE_NAME, mock(MultipartFile.class)));
  }

  @Test
  public void test_upload_rd_file_with_valid_parameters() throws ResourceExistsException, InternalErrorException {
    User sampleUser = mock(User.class);
    MultipartFile uploadedXmlFile = mock(MultipartFile.class);

    resourceService.uploadXmlFile(sampleUser, SAMPLE_FILE_NAME, uploadedXmlFile);

    verify(resourceDescriptionRepository, times(1)).save(any(ResourceDescription.class));
  }

  @Test
  public void test_replace_rd_file_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.replaceXmlFileById(null, SAMPLE_FILE_NAME, mock(MultipartFile.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.replaceXmlFileById(mock(User.class), null, mock((MultipartFile.class))));
    assertThrows(InternalErrorException.class, () -> resourceService.replaceXmlFileById(mock(User.class), StringUtils.EMPTY, mock((MultipartFile.class))));
    assertThrows(InternalErrorException.class, () -> resourceService.replaceXmlFileById(mock(User.class), SAMPLE_FILE_NAME, null));
  }

  @Test
  public void test_replace_rd_file_with_valid_parameters_not_matching_user_id() {
    User sampleUser = mock(User.class);
    MultipartFile uploadedXmlFile = mock(MultipartFile.class);
    ResourceDescription rdFromDB = initSampleRDFromDB();

    when(rdFromDB.getUserId()).thenReturn(1);
    when(sampleUser.getId()).thenReturn(2);

    assertThrows(InternalErrorException.class, () -> resourceService.replaceXmlFileById(sampleUser, SAMPLE_FILE_NAME, uploadedXmlFile));
  }

  @Test
  public void test_replace_rd_file_with_valid_parameters_matching_user_id()
    throws InternalErrorException, ResourceNotFoundException {
    User sampleUser = mock(User.class);
    MultipartFile uploadedXmlFile = mock(MultipartFile.class);
    ResourceDescription rdFromDB = initSampleRDFromDB();

    when(rdFromDB.getUserId()).thenReturn(1);
    when(sampleUser.getId()).thenReturn(1);

    resourceService.replaceXmlFileById(sampleUser, SAMPLE_FILE_NAME, uploadedXmlFile);

    verify(resourceDescriptionRepository, times(1)).save(any(ResourceDescription.class));
  }

  @Test
  public void test_change_rd_stage_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.changeRdStageById(null, ResourceStageConstant.IN_AUTOMATIC_VALIDATION));
    assertThrows(InternalErrorException.class, () -> resourceService.changeRdStageById(StringUtils.EMPTY, ResourceStageConstant.IN_AUTOMATIC_VALIDATION));
    assertThrows(InternalErrorException.class, () -> resourceService.changeRdStageById(SAMPLE_RD_ID, StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.changeRdStageById(SAMPLE_RD_ID, "a_weird_stage"));

    when(resourceDescriptionRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
    assertThrows(ResourceNotFoundException.class, () -> resourceService.changeRdStageById(SAMPLE_RD_ID, "not_exist_stage_id"));
  }

  @Test
  public void test_change_rd_stage_with_valid_parameters() throws InternalErrorException, ResourceNotFoundException {
    when(resourceDescriptionRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
    when(resourceStageRepository.existsById(anyString())).thenReturn(Boolean.TRUE);

    resourceService.changeRdStageById(SAMPLE_RD_ID, ResourceStageConstant.REVIEW_RETURNED);

    verify(resourceDescriptionRepository, times(1)).setRdStage(ResourceStageConstant.REVIEW_RETURNED.getIdVal(),
      SAMPLE_RD_ID);
  }

  @Test
  public void test_review_rd_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.reviewRdById(null, mock(ResourceReviewDTO.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.reviewRdById(StringUtils.EMPTY, mock(ResourceReviewDTO.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.reviewRdById(SAMPLE_RD_ID, null));
  }

  @Test
  public void test_review_rd_with_valid_parameters() throws InternalErrorException {
    resourceService.reviewRdById(SAMPLE_RD_ID, mock(ResourceReviewDTO.class));

    verify(resourceReviewRepository, times(1)).save(any(ResourceReview.class));
  }

  @Test
  public void test_update_rd_review_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDReviewById(null, mock(ResourceReviewDTO.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDReviewById(StringUtils.EMPTY, mock(ResourceReviewDTO.class)));
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDReviewById(SAMPLE_RD_ID, null));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.updateRDReviewById(SAMPLE_RD_ID, mock(ResourceReviewDTO.class)));
  }

  @Test
  public void test_update_rd_review_with_valid_parameters() throws InternalErrorException, ResourceNotFoundException {
    ResourceReview reviewFromDB = mock(ResourceReview.class);

    when(resourceReviewRepository.findById(anyString())).thenReturn(Optional.of(reviewFromDB));

    resourceService.updateRDReviewById(SAMPLE_RD_ID, mock(ResourceReviewDTO.class));

    verify(resourceReviewRepository, times(1)).save(reviewFromDB);
  }

  @Test
  public void test_delete_rd_review_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRDReviewById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRDReviewById(StringUtils.EMPTY));
  }

  @Test
  public void test_delete_rd_review_with_valid_parameters() throws InternalErrorException, ResourceNotFoundException {
    resourceService.deleteRDReviewById(SAMPLE_RD_ID);

    verify(resourceReviewRepository, times(1)).deleteById(SAMPLE_RD_ID);
  }

  @Test
  public void test_get_RD_supplementary_files_by_id_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceService.getRDSupplementaryFilesById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.getRDSupplementaryFilesById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getRDSupplementaryFilesById(SAMPLE_RD_ID));

    when(resourceDescriptionRepository.findById(anyString())).thenReturn(Optional.of(mock(ResourceDescription.class)));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getRDSupplementaryFilesById(SAMPLE_RD_ID));
  }

  @Test
  public void test_get_RD_supplementary_files_by_id_with_valid_parameters()
    throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription rdFileFromDB = initSampleRDFromDB();

    ResourceSupplementaryFile file_1 = mock(ResourceSupplementaryFile.class);
    ResourceSupplementaryFile file_2 = mock(ResourceSupplementaryFile.class);

    when(rdFileFromDB.getId()).thenReturn(SAMPLE_RD_ID);
    when(resourceSupplementRepository.findAllByResourceId(anyString())).thenReturn(Optional.of(List.of(file_1, file_2)));

    List<ResourceSupplementaryFile> files = resourceService.getRDSupplementaryFilesById(SAMPLE_RD_ID);

    verify(resourceSupplementRepository).findAllByResourceId(anyString());
    assertFalse(files.isEmpty());
  }

  @Test
  public void test_upload_supplementary_files_by_id_with_invalid_parameters() {
    MultipartFile sampleUploadedFile = mock(MultipartFile.class);
    long largeFileSize = (123 * 1024 * 1024);

    assertThrows(InternalErrorException.class, () -> resourceService.uploadSupplementaryFileToRDById(null, sampleUploadedFile));
    assertThrows(InternalErrorException.class, () -> resourceService.uploadSupplementaryFileToRDById(StringUtils.EMPTY, sampleUploadedFile));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.uploadSupplementaryFileToRDById(SAMPLE_RD_ID, sampleUploadedFile));
    assertThrows(InternalErrorException.class, () -> {
      when(resourceDescriptionRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
      resourceService.uploadSupplementaryFileToRDById(SAMPLE_RD_ID, null);
    });

    assertThrows(ResourceExceedsMaxSizeException.class, () -> {
      when(resourceDescriptionRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
      when(sampleUploadedFile.getSize()).thenReturn(largeFileSize);
      resourceService.uploadSupplementaryFileToRDById(SAMPLE_RD_ID, sampleUploadedFile);
    });
  }

  @Test
  public void test_upload_supplementary_files_by_id_with_valid_parameters()
    throws Exception {
    MultipartFile sampleUploadedFile = mock(MultipartFile.class);

    when(resourceDescriptionRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
    when(sampleUploadedFile.getSize()).thenReturn(123L);
    when(sampleUploadedFile.getBytes()).thenReturn(SAMPLE_RD_CONTENT.getBytes());

    resourceService.uploadSupplementaryFileToRDById(SAMPLE_RD_ID, sampleUploadedFile);

    verify(resourceSupplementRepository, times(1)).save(any(ResourceSupplementaryFile.class));
  }

  @Test
  public void test_get_supplementary_file_by_id_with_invalid_parameter() {
    assertThrows(InternalErrorException.class, () -> resourceService.getSupplementaryFileById(null, 1));
    assertThrows(InternalErrorException.class, () -> resourceService.getSupplementaryFileById(StringUtils.EMPTY, 2));
    assertThrows(InternalErrorException.class, () -> resourceService.getSupplementaryFileById(SAMPLE_RD_ID, null));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getSupplementaryFileById(SAMPLE_RD_ID, 3));
  }

  @Test
  public void test_get_supplementary_file_by_id_with_valid_parameter()
    throws InternalErrorException, ResourceNotFoundException {
    int sampleFileID = 4;

    when(resourceSupplementRepository.findByIdAndResourceId(sampleFileID, SAMPLE_RD_ID)).thenReturn(Optional.of(mock(ResourceSupplementaryFile.class)));

    resourceService.getSupplementaryFileById(SAMPLE_RD_ID, sampleFileID);

    verify(resourceSupplementRepository, times(1)).findByIdAndResourceId(sampleFileID, SAMPLE_RD_ID);
  }

  @Test
  public void test_upload_supplementary_file_to_RD_by_id_with_invalid_params() {
    MultipartFile sampleUploadedFile = mock(MultipartFile.class);
    ResourceSupplementaryFile supplementaryFileFromDB = mock(ResourceSupplementaryFile.class);
    Integer sampleSupFileId = 1;
    long smallFileSize = 123L;
    long largeFileSize = (smallFileSize * 1024 * 1024);


    assertThrows(InternalErrorException.class, () -> resourceService.updateRDSupplementaryFileById(null, sampleSupFileId, sampleUploadedFile));
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDSupplementaryFileById(StringUtils.EMPTY, sampleSupFileId, sampleUploadedFile));
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, null, sampleUploadedFile));
    assertThrows(InternalErrorException.class, () -> resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, sampleSupFileId, null));

    assertThrows(ResourceExceedsMaxSizeException.class, () -> {
      when(sampleUploadedFile.getSize()).thenReturn(largeFileSize);
      resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, sampleSupFileId, sampleUploadedFile);
    });

    assertThrows(ResourceNotFoundException.class, () -> {
      when(sampleUploadedFile.getSize()).thenReturn(smallFileSize);
      resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, sampleSupFileId, sampleUploadedFile);
    });

    assertThrows(InternalErrorException.class, () -> {
      when(sampleUploadedFile.getSize()).thenReturn(smallFileSize);
      when(supplementaryFileFromDB.getFileName()).thenReturn("abc");
      when(sampleUploadedFile.getOriginalFilename()).thenReturn("cba");
      when(resourceSupplementRepository.findByIdAndResourceId(any(Integer.class), anyString())).thenReturn(Optional.of(supplementaryFileFromDB));

      resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, sampleSupFileId, sampleUploadedFile);
    });

  }

  @Test
  public void test_upload_supplementary_file_to_RD_by_id_with_valid_params()
    throws Exception {
    MultipartFile sampleUploadedFile = mock(MultipartFile.class);
    ResourceSupplementaryFile supplementaryFileFromDB = mock(ResourceSupplementaryFile.class);
    Integer sampleSupFileId = 1;

    when(sampleUploadedFile.getSize()).thenReturn(123L);
    when(sampleUploadedFile.getOriginalFilename()).thenReturn("abc");
    when(sampleUploadedFile.getBytes()).thenReturn(SAMPLE_RD_CONTENT.getBytes());
    when(supplementaryFileFromDB.getFileName()).thenReturn("abc");
    when(resourceSupplementRepository.findByIdAndResourceId(any(Integer.class), anyString())).thenReturn(Optional.of(supplementaryFileFromDB));

    resourceService.updateRDSupplementaryFileById(SAMPLE_RD_ID, sampleSupFileId, sampleUploadedFile);

    verify(resourceSupplementRepository, times(1)).save(supplementaryFileFromDB);
  }

  @Test
  public void test_delete_rd_supplementary_file_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRDSupplementaryFileById(null, SAMPLE_RD_SUPPLEMENTARY_FILE_ID));
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRDSupplementaryFileById(StringUtils.EMPTY, SAMPLE_RD_SUPPLEMENTARY_FILE_ID));
    assertThrows(InternalErrorException.class, () -> resourceService.deleteRDSupplementaryFileById(SAMPLE_RD_ID, null));
  }

  @Test
  public void test_delete_rd_supplementary_file_by_id_with_valid_params() throws InternalErrorException {
    resourceService.deleteRDSupplementaryFileById(SAMPLE_RD_ID, SAMPLE_RD_SUPPLEMENTARY_FILE_ID);

    verify(resourceSupplementRepository, times(1)).deleteByIdAndResourceId(SAMPLE_RD_SUPPLEMENTARY_FILE_ID, SAMPLE_RD_ID);
  }

  @Test
  public void test_get_rd_validation_report_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> resourceService.getRDValidationReportById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.getRDValidationReportById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getRDValidationReportById(SAMPLE_RD_ID));
  }

  @Test
  public void test_get_rd_validation_report_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(resourceValidationResultRepository.findById(SAMPLE_RD_ID)).thenReturn(Optional.of(mock(
      ResourceValidationResult.class)));

    resourceService.getRDValidationReportById(SAMPLE_RD_ID);

    verify(resourceValidationResultRepository, times(1)).findById(SAMPLE_RD_ID);
  }

  @Test
  public void test_get_rd_review_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> resourceService.getRDReviewById(null));
    assertThrows(InternalErrorException.class, () -> resourceService.getRDReviewById(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getRDReviewById(SAMPLE_RD_ID));
  }

  @Test
  public void test_get_rd_review_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(resourceReviewRepository.findById(SAMPLE_RD_ID)).thenReturn(Optional.of(mock(ResourceReview.class)));

    resourceService.getRDReviewById(SAMPLE_RD_ID);

    verify(resourceReviewRepository, times(1)).findById(SAMPLE_RD_ID);
  }

  private ResourceDescription initSampleRDFromDB() {
    ResourceDescription sampleRD = mock(ResourceDescription.class);

    when(resourceDescriptionRepository.findById(anyString())).thenReturn(Optional.of(sampleRD));

    return sampleRD;
  }
}