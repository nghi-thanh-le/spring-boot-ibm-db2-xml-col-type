package fi.tuni.resourcedescription.service.resource.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.capabilitiy.Capability;
import fi.tuni.resourcedescription.model.rcp.standard.Standard;
import fi.tuni.resourcedescription.model.rcp.standard.StandardBody;
import fi.tuni.resourcedescription.payload.request.standard.StandardBodyDTO;
import fi.tuni.resourcedescription.payload.request.standard.StandardDTO;
import fi.tuni.resourcedescription.repository.standard.StandardBodyRepository;
import fi.tuni.resourcedescription.repository.standard.StandardRepository;
import fi.tuni.resourcedescription.service.resource.ResourceCatalogueService;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ResourceCatalogueServiceImpl implements ResourceCatalogueService {
  private static final Logger log = LoggerFactory.getLogger(ResourceStageServiceImpl.class);

  private final StandardRepository standardRepository;
  private final StandardBodyRepository standardBodyRepository;
  private final ResourceQueryService resourceQueryService;

  @Autowired
  public ResourceCatalogueServiceImpl(StandardRepository standardRepository,
                                      StandardBodyRepository standardBodyRepository,
                                      ResourceQueryService resourceQueryService) {
    this.standardBodyRepository = standardBodyRepository;
    this.standardRepository = standardRepository;
    this.resourceQueryService = resourceQueryService;
  }

  @Override
  public List<Standard> getStandards() {
    return standardRepository.getAllStandards();
  }

  @Override
  public Standard getStandardById(Integer id) throws ResourceNotFoundException {
    //    TODO: resolve org.springframework.http.converter.HttpMessageNotWritableInternalErrorException: Could not write JSON: could not extract ResultSet;
//     nested exception is com.fasterxml.jackson.databind.JsonMappingInternalErrorException: could not extract ResultSet (through reference chain: fi.tuni.resourcedescription.model.resdescasemplws.standard.Standard$HibernateProxy$CjC0Ur2V["stdId"])
//    return standardRepository.getById(Integer.valueOf(id));
    return getStandards()
      .stream()
      .filter(standard -> standard.getId().equals(id))
      .findFirst()
      .orElseThrow(() -> new ResourceNotFoundException("Cannot find Standard with id " + id));
  }

  @Override
  public Standard addNewStandard(StandardDTO standard) throws InternalErrorException {
    if (Objects.isNull(standard)) {
      throw new InternalErrorException("Cannot add new standard as it is null");
    }

    Standard standardToSave = new Standard();
    standardToSave.setId(getStandards().size() + 1);
    updateStandardInfo(standardToSave, standard);

    StandardBody standardBodyToSave = null;
    if (Objects.nonNull(standard.getBody())) {
       standardBodyToSave = new StandardBody();
      standardBodyToSave.setId(standardBodyRepository.findAll().size() + 1);
      updateStandardBodyInfo(standardBodyToSave, standard.getBody());

      standardToSave.setBody(standardBodyToSave);
    }

    try {
      if (Objects.nonNull(standardBodyToSave)) {
        standardBodyRepository.save(standardBodyToSave);
      }
      standardRepository.save(standardToSave);
      return standardToSave;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public Standard updateStandardById(Integer id, StandardDTO standard)
    throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id) || Objects.isNull(standard)) {
      throw new InternalErrorException("Invalid id or standard to update");
    }

    Standard standardToUpdate = getStandardById(id);
    updateStandardInfo(standardToUpdate, standard);

    if (Objects.nonNull(standard.getBody())) {
      updateStandardBodyInfo(standardToUpdate.getBody(), standard.getBody());
    }

    try {
      if (Objects.nonNull(standardToUpdate.getBody())) {
        standardBodyRepository.save(standardToUpdate.getBody());
      }
      standardRepository.save(standardToUpdate);
      return standardToUpdate;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public boolean deleteStandardById(Integer id) throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Invalid standard id.");
    }

    Standard standard = getStandardById(id);

    Integer standardBodyId = null;
    if (Objects.nonNull(standard.getBody())) {
      standardBodyId = standard.getBody().getId();
    }

    try {
      if (Objects.nonNull(standardBodyId)) {
        standard.setBody(null);
        standardRepository.save(standard);
        standardBodyRepository.deleteById(standardBodyId);
      }
      standardRepository.save(standard);
      standardRepository.deleteById(id);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public Long getTotalNumberOfStandards() {
    return standardRepository.count();
  }

  @Override
  @Cacheable("capabilities")
  public List<Capability> getCapabilities() {
    return Collections.emptyList();

  }

  @Override
  public List<Capability> getCapabilitiesByName(String name) throws InternalErrorException {
    if (StringUtils.isBlank(name)) {
      throw new InternalErrorException("Invalid name to find capability!");
    }

    return getCapabilities()
      .stream()
      .filter(capability -> StringUtils.containsIgnoreCase(capability.getName(), name))
      .collect(Collectors.toList());
  }

  @Override
  public Capability getCapabilityByName(String name) throws ResourceNotFoundException {
    if (StringUtils.isBlank(name)) {
      throw new ResourceNotFoundException("There is no capability with empty or null name.");
    }

    return getCapabilities()
      .stream()
      .filter(capability -> capability.getName().equalsIgnoreCase(name))
      .findFirst()
      .orElseThrow(() -> new ResourceNotFoundException("Capability with name " + name + " does not exist!"));
  }

  private void updateStandardInfo(Standard standardToUpdate, StandardDTO sourceStandard) {
    standardToUpdate.setStdId(sourceStandard.getStdId());
    standardToUpdate.setCode(sourceStandard.getCode());
    standardToUpdate.setName(sourceStandard.getName());
    standardToUpdate.setDescription(sourceStandard.getDescription());
    standardToUpdate.setUrl(sourceStandard.getUrl());
  }

  private void updateStandardBodyInfo(StandardBody standardBodyToUpdate, StandardBodyDTO sourceStandardBody) {
    standardBodyToUpdate.setName(sourceStandardBody.getName());
    standardBodyToUpdate.setBodyId(sourceStandardBody.getBodyId());
    standardBodyToUpdate.setDescription(sourceStandardBody.getDescription());
    standardBodyToUpdate.setUrl(sourceStandardBody.getUrl());
  }
}
