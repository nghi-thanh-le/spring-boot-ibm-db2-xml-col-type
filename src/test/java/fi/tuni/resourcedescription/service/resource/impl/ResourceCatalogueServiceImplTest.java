package fi.tuni.resourcedescription.service.resource.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.capabilitiy.Capability;
import fi.tuni.resourcedescription.model.rcp.standard.Standard;
import fi.tuni.resourcedescription.model.rcp.standard.StandardBody;
import fi.tuni.resourcedescription.payload.request.standard.StandardBodyDTO;
import fi.tuni.resourcedescription.payload.request.standard.StandardDTO;
import fi.tuni.resourcedescription.repository.standard.StandardBodyRepository;
import fi.tuni.resourcedescription.repository.standard.StandardRepository;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResourceCatalogueServiceImplTest {
  private static final Integer SAMPLE_STANDARD_ID_1 = 1;
  private static final Integer SAMPLE_STANDARD_ID_2 = 2;
  private static final String SAMPLE_CAPA_NAME = "foo";

  @Mock
  private StandardRepository standardRepository;
  @Mock
  private StandardBodyRepository standardBodyRepository;
  @Mock
  private ResourceQueryService resourceQueryService;

  @InjectMocks
  private ResourceCatalogueServiceImpl resourceCatalogueService;

  @Test
  public void test_get_all_standards() {
    resourceCatalogueService.getStandards();
    verify(standardRepository).getAllStandards();
  }

  @Test
  public void test_get_standards_from_an_empty_list_by_id_but_not_exists_expect_thrown_exception() {
    when(standardRepository.getAllStandards()).thenReturn(Collections.emptyList());

    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.getStandardById(SAMPLE_STANDARD_ID_1));
  }

  @Test
  public void test_get_standard_by_id_but_does_not_exist_expect_thrown_exception() {
    Standard sampleStandard = mock(Standard.class);

    when(standardRepository.getAllStandards()).thenReturn(List.of(sampleStandard));
    when(sampleStandard.getId()).thenReturn(SAMPLE_STANDARD_ID_1);

    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.getStandardById(SAMPLE_STANDARD_ID_2));
  }

  @Test
  public void test_get_standard_by_id_but_does_not_exist_expect_return_valid_standard()
    throws ResourceNotFoundException {
    Standard sampleStandard = mock(Standard.class);

    when(standardRepository.getAllStandards()).thenReturn(List.of(sampleStandard));
    when(sampleStandard.getId()).thenReturn(SAMPLE_STANDARD_ID_1);

    Standard foundStandard = resourceCatalogueService.getStandardById(SAMPLE_STANDARD_ID_1);

    assertEquals(foundStandard.getId(), sampleStandard.getId());
  }

  @Test
  public void test_add_standard_with_a_null_object() {
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.addNewStandard(null));
  }

  @Test
  public void test_add_standard_but_then_expect_return_saved_value() throws InternalErrorException {
    StandardDTO sampleData = initStandardDTO(false);

    resourceCatalogueService.addNewStandard(sampleData);
    verifyZeroInteractions(standardBodyRepository);
    verify(standardRepository).save(any(Standard.class));
  }

  @Test
  public void test_update_standard_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.updateStandardById(null, mock(StandardDTO.class)));
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.updateStandardById(123, null));
    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.updateStandardById(123, mock(StandardDTO.class)));
  }

  @Test
  public void test_update_standard_with_valid_parameters() throws InternalErrorException, ResourceNotFoundException {
    StandardDTO dataFromRequest = initStandardDTO(true);
    Standard standardFromDB = mock(Standard.class);
    StandardBody standardBodyFromDB = mock(StandardBody.class);

    when(standardRepository.getAllStandards()).thenReturn(List.of(standardFromDB));
    when(standardFromDB.getId()).thenReturn(SAMPLE_STANDARD_ID_1);
    when(standardFromDB.getBody()).thenReturn(standardBodyFromDB);

    resourceCatalogueService.updateStandardById(SAMPLE_STANDARD_ID_1, dataFromRequest);

    verify(standardRepository).save(any(Standard.class));
    verify(standardBodyRepository).save(any(StandardBody.class));
  }

  @Test
  public void test_delete_std_by_invalid_id() {
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.deleteStandardById(null));
    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.deleteStandardById(123));
  }

  @Test
  public void test_delete_std_by_valid_id_but_does_not_exists()
    throws InternalErrorException, ResourceNotFoundException {
    Standard sampleStandardFromDB = mock(Standard.class);

    when(standardRepository.getAllStandards()).thenReturn(List.of(sampleStandardFromDB));
    when(sampleStandardFromDB.getId()).thenReturn(SAMPLE_STANDARD_ID_2);

    resourceCatalogueService.deleteStandardById(SAMPLE_STANDARD_ID_2);

    verify(standardRepository).save(any(Standard.class));
    verify(standardRepository).deleteById(SAMPLE_STANDARD_ID_2);
  }

  @Test
  public void test_get_total_numbers_is_called_from_repository() {
    resourceCatalogueService.getTotalNumberOfStandards();

    verify(standardRepository).count();
  }

  @Test
  public void test_get_capabilities_called_from_client_lib() {
    List<Capability> capabilities = resourceCatalogueService.getCapabilities();

    assertTrue(capabilities.isEmpty());
  }

  @Test
  @Disabled
  public void test_get_capabilities_receives_correct_data() throws InternalErrorException {
    // initSampleTuniCapaModels();

    List<Capability> capabilities = resourceCatalogueService.getCapabilities();
    assertEquals(1, capabilities.size());
    assertEquals(capabilities.get(0).getName(), SAMPLE_CAPA_NAME);
  }

  @Test
  public void test_get_capas_by_name_with_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.getCapabilitiesByName(StringUtils.EMPTY));
    assertThrows(InternalErrorException.class, () -> resourceCatalogueService.getCapabilitiesByName(null));
  }

  @Test
  @Disabled
  public void test_get_capas_by_name() throws InternalErrorException {
    // initSampleTuniCapaModels();

    List<Capability> capabilities = resourceCatalogueService.getCapabilitiesByName(SAMPLE_CAPA_NAME);
    assertEquals(1, capabilities.size());
    assertEquals(capabilities.get(0).getName(), SAMPLE_CAPA_NAME);
  }

  @Test
  public void test_get_single_capa_by_name_with_invalid_name() {
    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.getCapabilityByName(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> resourceCatalogueService.getCapabilityByName(null));
  }

  @Test
  @Disabled
  public void test_get_single_capa_by_name_with_valid_name() throws ResourceNotFoundException {
    // initSampleTuniCapaModels();

    Capability foundCapa = resourceCatalogueService.getCapabilityByName(SAMPLE_CAPA_NAME);

    assertNotNull(foundCapa);
    assertEquals(SAMPLE_CAPA_NAME, foundCapa.getName());
  }

  private StandardDTO initStandardDTO(boolean initBody) {
    StandardDTO sampleData = new StandardDTO();
    sampleData.setStdId("std.123");
    sampleData.setCode("123");
    sampleData.setDescription("Std description");
    sampleData.setName("name");
    sampleData.setUrl("http://sample.url");

    if (initBody) {
      StandardBodyDTO sampleDataBody = new StandardBodyDTO();
      sampleDataBody.setBodyId("std.123");
      sampleDataBody.setName("bd name");
      sampleDataBody.setUrl("http://sample.body.url");
      sampleDataBody.setDescription("Std.bd description");

      sampleData.setBody(sampleDataBody);
    }

    return sampleData;
  }
}