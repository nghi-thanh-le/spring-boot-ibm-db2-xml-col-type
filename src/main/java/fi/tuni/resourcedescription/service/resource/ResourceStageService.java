package fi.tuni.resourcedescription.service.resource;

import java.util.List;
import org.springframework.lang.Nullable;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceStage;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;

public interface ResourceStageService {
  List<ResourceStage> getStages();
  @Nullable
  ResourceStage getResourceStageById(ResourceStageConstant id);
}
