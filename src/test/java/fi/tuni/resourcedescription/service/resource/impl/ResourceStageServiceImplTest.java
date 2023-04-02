package fi.tuni.resourcedescription.service.resource.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceStage;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceStageConstant;
import fi.tuni.resourcedescription.repository.resourcedescription.ResourceStageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResourceStageServiceImplTest {

  @Mock
  private ResourceStageRepository mockedResourceStageRepo;
  private ResourceStageServiceImpl resourceStageService;

  @BeforeEach
  protected void init() {
    resourceStageService = new ResourceStageServiceImpl(mockedResourceStageRepo);
  }

  @Test
  public void test_get_all_stages_return_result_from_repository() {
    resourceStageService.getStages();
    verify(mockedResourceStageRepo).findAll();
  }

  @Test
  public void test_get_resource_stage_by_id_does_not_exists_in_db_return_null() {
    when(mockedResourceStageRepo.existsById(anyString())).thenReturn(Boolean.FALSE);

    assertNull(resourceStageService.getResourceStageById(ResourceStageConstant.UPLOAD));
    verify(mockedResourceStageRepo).existsById(anyString());
  }

  @Test
  public void test_get_resource_stage_by_id_exists_in_db() {
    when(mockedResourceStageRepo.existsById(anyString())).thenReturn(Boolean.TRUE);
    when(mockedResourceStageRepo.getById(anyString())).thenReturn(mock(ResourceStage.class));

    assertNotNull(resourceStageService.getResourceStageById(ResourceStageConstant.UPLOAD));
    verify(mockedResourceStageRepo).existsById(anyString());
  }
}