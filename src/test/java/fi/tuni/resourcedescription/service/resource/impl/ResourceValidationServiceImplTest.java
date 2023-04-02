package fi.tuni.resourcedescription.service.resource.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceValidationResultRepository;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import fi.tuni.resourcedescription.service.utils.XmlErrorHandler;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXParseException;

@ExtendWith(MockitoExtension.class)
public class ResourceValidationServiceImplTest {
  private static final String SAMPLE_RD_ID = "sample_rd_id";
  private static final String SAMPLE_XML_CONTENT = "<sample>content</sample>";

  @Mock
  private ResourceService resourceService;
  @Mock
  private ByteArrayTransformationService xmlBytesTransformService;
  @Mock
  private ResourceValidationResultRepository resourceValidationResultRepository;
  @Mock
  private XmlUtils xmlUtils;

  @InjectMocks
  private ResourceValidationServiceImpl resourceValidationService;

  @Test
  public void test_validate_rd_with_invalid_rd_id() {
    assertThrows(InternalErrorException.class, () -> resourceValidationService.validateRdById(null));
    assertThrows(InternalErrorException.class, () -> resourceValidationService.validateRdById(StringUtils.EMPTY));

    assertThrows(ResourceNotFoundException.class, () -> {
      when(resourceService.getRDById(anyString())).thenThrow(ResourceNotFoundException.class);
      resourceValidationService.validateRdById("does not exist!");
    });
  }

  @Test
  public void test_validate_exising_rd_on_successful_case() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription rdFile = mock(ResourceDescription.class);
    XmlErrorHandler xmlErrorHandler = mock(XmlErrorHandler.class);

    when(rdFile.getContent()).thenReturn(SAMPLE_XML_CONTENT.getBytes());
    when(resourceService.getRDById(anyString())).thenReturn(rdFile);
    when(xmlBytesTransformService.transformBytesArray(any(byte[].class), any(Boolean.class))).thenReturn(SAMPLE_XML_CONTENT);
    when(xmlUtils.validateXmlContent(SAMPLE_XML_CONTENT)).thenReturn(xmlErrorHandler);
    when(xmlErrorHandler.getParseExceptions()).thenReturn(Collections.emptyList());

    resourceValidationService.validateRdById(SAMPLE_RD_ID);

    verify(resourceService).changeRdStageById(SAMPLE_RD_ID, ResourceStageConstant.IN_AUTOMATIC_VALIDATION.getIdVal());
    verify(resourceService).changeRdStageById(SAMPLE_RD_ID, ResourceStageConstant.REVIEW_APPROVED);
    verify(resourceValidationResultRepository).save(any(ResourceValidationResult.class));
  }

  @Test
  public void test_validate_exising_rd_on_failure_case() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription rdFile = mock(ResourceDescription.class);
    XmlErrorHandler xmlErrorHandler = mock(XmlErrorHandler.class);

    when(rdFile.getContent()).thenReturn(SAMPLE_XML_CONTENT.getBytes());
    when(resourceService.getRDById(anyString())).thenReturn(rdFile);
    when(xmlBytesTransformService.transformBytesArray(any(byte[].class), any(Boolean.class))).thenReturn(SAMPLE_XML_CONTENT);
    when(xmlUtils.validateXmlContent(SAMPLE_XML_CONTENT)).thenReturn(xmlErrorHandler);
    when(xmlErrorHandler.getParseExceptions()).thenReturn(List.of(mock(SAXParseException.class)));

    resourceValidationService.validateRdById(SAMPLE_RD_ID);

    verify(resourceService).changeRdStageById(SAMPLE_RD_ID, ResourceStageConstant.IN_AUTOMATIC_VALIDATION.getIdVal());
    verify(resourceService).changeRdStageById(SAMPLE_RD_ID, ResourceStageConstant.REVIEW_RETURNED);
    verify(resourceValidationResultRepository).save(any(ResourceValidationResult.class));
  }
}