package fi.tuni.resourcedescription.service.resource.impl;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceStage;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceStageRepository;
import fi.tuni.resourcedescription.service.resource.ResourceStageService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ResourceStageServiceImpl implements ResourceStageService {
  private static final Logger log = LoggerFactory.getLogger(ResourceStageServiceImpl.class);
  private final ResourceStageRepository resourceStageRepository;
  private final Map<ResourceStageConstant, ResourceStage> stagesMap;

  @Autowired
  public ResourceStageServiceImpl(ResourceStageRepository resourceStageRepository) {
    this.resourceStageRepository = resourceStageRepository;
    this.stagesMap = new HashMap<>();
  }

  @Override
  public List<ResourceStage> getStages() {
    return resourceStageRepository.findAll();
  }

  @Override
  @Nullable
  public ResourceStage getResourceStageById(ResourceStageConstant id) {
    if (stagesMap.containsKey(id)) {
      return stagesMap.get(id);
    }

    if (resourceStageRepository.existsById(id.getIdVal())) {
      ResourceStage stageFromDb = resourceStageRepository.getById(id.getIdVal());
      stagesMap.put(id, stageFromDb);
      return stageFromDb;
    }

    log.error("Stage with id " + id.getIdVal() + " does not exist in DB.");
    return null;
  }
}
