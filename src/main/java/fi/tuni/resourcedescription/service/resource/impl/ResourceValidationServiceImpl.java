package fi.tuni.resourcedescription.service.resource.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceValidationResultRepository;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.resource.ResourceValidationService;
import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import fi.tuni.resourcedescription.service.utils.XmlErrorHandler;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ResourceValidationServiceImpl implements ResourceValidationService {
  private static final Logger log = LoggerFactory.getLogger(ResourceValidationServiceImpl.class);

  private final ResourceService resourceService;
  private final ByteArrayTransformationService xmlBytesTransformService;
  private final ResourceValidationResultRepository resourceValidationResultRepository;
  private final XmlUtils xmlUtils;

  private static final String ERROR_FORMAT = "Line number: %d - Column number: %d - Error: %s;";
  private static final String PASS_STATE = "PASS";
  private static final String FAIL_STATE = "ERROR";

  @Autowired
  public ResourceValidationServiceImpl(ResourceService resourceService,
                                       ByteArrayTransformationService xmlBytesTransformService,
                                       ResourceValidationResultRepository resourceValidationResultRepository,
                                       XmlUtils xmlUtils) {
    this.resourceService = resourceService;
    this.xmlBytesTransformService = xmlBytesTransformService;
    this.resourceValidationResultRepository = resourceValidationResultRepository;
    this.xmlUtils = xmlUtils;
  }

  @Override
  public ResourceValidationResult validateRdById(String rdId) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(rdId)) {
      throw new InternalErrorException("Cannot validate a null / empty resource id.");
    }

    ResourceDescription rdFile = resourceService.getRDById(rdId);
    // TODO: missing a validation on changing stage of certain stages
    resourceService.changeRdStageById(rdId, ResourceStageConstant.IN_AUTOMATIC_VALIDATION.getIdVal());

    ResourceValidationResult resourceValidationResult = resourceValidationResultRepository
      .findById(rdId)
      .orElseGet(() -> {
        ResourceValidationResult rdValidationRes = new ResourceValidationResult();
        rdValidationRes.setId(rdId);
        return rdValidationRes;
      });

    try {
      String beautifiedXmlContent = xmlBytesTransformService.transformBytesArray(rdFile.getContent(), true);
      XmlErrorHandler xmlErrorHandler = xmlUtils.validateXmlContent(beautifiedXmlContent);

      if (xmlErrorHandler.getParseExceptions().isEmpty()) {
        resourceValidationResult.setState(PASS_STATE);
        resourceValidationResult.setValidationMessage(StringUtils.EMPTY);
        resourceService.changeRdStageById(rdId, ResourceStageConstant.REVIEW_APPROVED);
      } else {
        resourceValidationResult.setState(FAIL_STATE);
        StringBuilder stringBuilder = new StringBuilder(StringUtils.EMPTY);
        xmlErrorHandler
          .getParseExceptions()
          .forEach(e -> {
            stringBuilder.append(String.format(ERROR_FORMAT, e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
          });
        resourceValidationResult.setValidationMessage(stringBuilder.toString());
        resourceService.changeRdStageById(rdId, ResourceStageConstant.REVIEW_RETURNED);
      }
      return resourceValidationResultRepository.save(resourceValidationResult);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }
}
