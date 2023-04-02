package fi.tuni.resourcedescription.service.resource.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.rowmapper.StringRowMapper;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.utils.QueryResultUtil;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.xml.sax.SAXException;

@ExtendWith(MockitoExtension.class)
public class ResourceQueryServiceImplTest {
  private static final String VALID_STD_ID = "valid_std_id";
  private static final String VALID_CAPA_NAME = "valid_capa_name";
  private static final String VALID_NODE_NAME = "valid_node_name";
  private static final String VALID_ECLASSES_ID = "valid_eclasses_name";
  private static final String SAMPLE_RD_ID_1 = "rd_id_1";
  private static final String SAMPLE_RD_ID_2 = "rd_id_2";

  @Mock
  private ResourceService resourceService;
  @Mock
  private QueryResultUtil queryResultUtil;
  @Mock
  private JdbcTemplate jdbcTemplate;
  @Mock
  private XmlUtils xmlUtils;

  @InjectMocks
  private ResourceQueryServiceImpl resourceQueryService;

  @Test
  public void test_get_simplified_rds_by_invalid_id() throws InternalErrorException, ResourceNotFoundException {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getSimplifiedRDById(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getSimplifiedRDById(StringUtils.EMPTY));

    when(resourceService.getRDById(anyString())).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> resourceQueryService.getSimplifiedRDById("Does not exists!"));
  }

  @Test
  public void test_get_simplified_rds_by_valid_id() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription resourceDescription = mock(ResourceDescription.class);

    when(resourceDescription.getContent()).thenReturn("<sample>content</sample>".getBytes());
    when(resourceService.getRDById(anyString())).thenReturn(resourceDescription);

    resourceQueryService.getSimplifiedRDById("a_sample_id.xml");

    verify(xmlUtils).xmlStringViaXsl(resourceDescription.getContent(), ResourceQueryServiceImpl.SIMPLIFIED_XSL_PATH);
  }

  @Test
  public void test_get_RDS_implementing_std_by_invalid_std_id() {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingStdByStdId(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingStdByStdId(StringUtils.EMPTY));
  }

  @Test
  public void test_get_RDS_implementing_std_by_valid_std_id_but_none_result() throws InternalErrorException {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

    assertTrue(resourceQueryService.getRDsImplementingStdByStdId(VALID_STD_ID).isEmpty());
  }

  @Test
  public void test_get_RDS_implementing_std_by_valid_std_id() throws InternalErrorException, ResourceNotFoundException {
    initSampleRDsForJdbcQuery();

    List<ResourceDescription> rdFiles = resourceQueryService.getRDsImplementingStdByStdId(VALID_STD_ID);

    assertEquals(2, rdFiles.size());
  }

  @Test
  public void test_get_RDS_implementing_capas_by_invalid_capa_name() {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingCapabilityByName(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingCapabilityByName(StringUtils.EMPTY));
  }

  @Test
  public void test_get_RDS_implementing_capas_by_valid_capa_name_but_none_result() throws InternalErrorException {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

    assertTrue(resourceQueryService.getRDsImplementingCapabilityByName(VALID_CAPA_NAME).isEmpty());
  }

  @Test
  public void test_get_RDS_implementing_capas_by_valid_capa_name() throws InternalErrorException, ResourceNotFoundException {
    initSampleRDsForJdbcQuery();

    List<ResourceDescription> rdFiles = resourceQueryService.getRDsImplementingCapabilityByName(VALID_CAPA_NAME);

    assertEquals(2, rdFiles.size());
  }

  @Test
  public void test_get_Rd_nodes_by_invalid_paramters() throws InternalErrorException, ResourceNotFoundException {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdAllNodesByName(null, VALID_NODE_NAME));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdAllNodesByName(StringUtils.EMPTY, VALID_NODE_NAME));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdAllNodesByName(SAMPLE_RD_ID_1, null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdAllNodesByName(SAMPLE_RD_ID_2, StringUtils.EMPTY));

    when(resourceService.getRDById(anyString())).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> resourceQueryService.getRdAllNodesByName(SAMPLE_RD_ID_1, VALID_NODE_NAME));
  }

  @Test
  public void test_get_Rd_nodes_by_valid_paramters()
    throws InternalErrorException, ResourceNotFoundException, JAXBException, ParserConfigurationException, IOException,
    TransformerException, SAXException {
    ResourceDescription sampleRDFromDB = mock(ResourceDescription.class);

    when(sampleRDFromDB.getId()).thenReturn(SAMPLE_RD_ID_1);
    when(resourceService.getRDById(SAMPLE_RD_ID_1)).thenReturn(sampleRDFromDB);
    when(jdbcTemplate.queryForObject(anyString(), any(StringRowMapper.class))).thenReturn("sample query");

    resourceQueryService.getRdAllNodesByName(SAMPLE_RD_ID_1, VALID_NODE_NAME);

    verify(jdbcTemplate).queryForObject(anyString(), any(StringRowMapper.class));
    verify(queryResultUtil).build("sample query");
  }

  @Test
  public void test_get_all_eclasses()
    throws Exception {
    resourceQueryService.getAllEClasses();

    verify(jdbcTemplate).query(anyString(), any(StringRowMapper.class));
    verify(queryResultUtil).build(any(List.class));
  }

  @Test
  public void test_get_eclassses_by_invalid_paramters() {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getEClassesById(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getEClassesById(StringUtils.EMPTY));
  }

  @Test
  public void test_get_eclassses_by_valid_paramters() throws Exception {
    resourceQueryService.getEClassesById(VALID_ECLASSES_ID);

    verify(jdbcTemplate).query(anyString(), any(StringRowMapper.class));
    verify(queryResultUtil).build(any(List.class));
  }

  @Test
  public void test_get_RDS_implementing_eclasses_by_invalid_eclasses_id() {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingEClassesById(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRDsImplementingEClassesById(StringUtils.EMPTY));
  }

  @Test
  public void test_get_RDS_implementing_eclasses_by_valid_eclasses_id_but_none_result() throws InternalErrorException {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

    assertTrue(resourceQueryService.getRDsImplementingEClassesById(VALID_STD_ID).isEmpty());
  }

  @Test
  public void test_get_RDS_implementing_eclasses_by_valid_eclasses_id() throws InternalErrorException, ResourceNotFoundException {
    initSampleRDsForJdbcQuery();

    List<ResourceDescription> rdFiles = resourceQueryService.getRDsImplementingEClassesById(VALID_STD_ID);

    assertEquals(2, rdFiles.size());
  }

  @Test
  public void test_get_standards_by_invalid_paramters() throws InternalErrorException, ResourceNotFoundException {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDById(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDById(StringUtils.EMPTY));

    when(resourceService.getRDById(anyString())).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> resourceQueryService.getStdsOfRDById(VALID_STD_ID));
  }

  @Test
  public void test_get_standards_by_valid_paramters() throws Exception {
    initRDFromDBWithFileName();

    resourceQueryService.getStdsOfRDById(VALID_STD_ID);

    verify(jdbcTemplate).query(anyString(), any(StringRowMapper.class));
    verify(queryResultUtil).build(any(List.class));
  }

  @Test
  public void test_get_stds_of_rds_with_invalid_parameters() throws InternalErrorException, ResourceNotFoundException {
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDByIdAndStdVal(null, VALID_STD_ID));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDByIdAndStdVal(StringUtils.EMPTY, VALID_STD_ID));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDByIdAndStdVal(VALID_STD_ID, null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getStdsOfRDByIdAndStdVal(VALID_STD_ID, StringUtils.EMPTY));

    when(resourceService.getRDById(anyString())).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> resourceQueryService.getStdsOfRDByIdAndStdVal(VALID_STD_ID, VALID_STD_ID));
  }

  @Test
  public void test_get_stds_of_rds_with_valid_params()
    throws Exception {
    initRDFromDBWithFileName();

    resourceQueryService.getStdsOfRDByIdAndStdVal(VALID_STD_ID, VALID_STD_ID);

    verify(jdbcTemplate).query(anyString(), any(StringRowMapper.class));
    verify(queryResultUtil).build(any(List.class));
  }

  @Test
  public void test_get_all_categories() throws InternalErrorException {
    resourceQueryService.getAllCategories();

    verify(jdbcTemplate).query(anyString(), any(StringRowMapper.class));
  }


  @Test
  public void test_get_rds_by_category_name_with_invalid_paramters(){
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdsByCategoryName(null));
    assertThrows(InternalErrorException.class, () -> resourceQueryService.getRdsByCategoryName(StringUtils.EMPTY));
  }

  @Test
  public void test_get_RDS_by_valid_category_name_but_none_result() throws InternalErrorException {
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

    assertTrue(resourceQueryService.getRdsByCategoryName(VALID_STD_ID).isEmpty());
  }

  @Test
  public void test_get_RDS_by_valid_category_name() throws InternalErrorException, ResourceNotFoundException {
    initSampleRDsForJdbcQuery();

    List<ResourceDescription> rdFiles = resourceQueryService.getRdsByCategoryName(VALID_STD_ID);

    assertEquals(2, rdFiles.size());
  }

  private void initSampleRDsForJdbcQuery() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription mappedSampleRD_1 = mock(ResourceDescription.class);
    ResourceDescription mappedSampleRD_2 = mock(ResourceDescription.class);
    ResourceDescription mappedSampleRD_3 = mock(ResourceDescription.class);

    ResourceDescription sampleRD_1 = mock(ResourceDescription.class);
    ResourceDescription sampleRD_2 = mock(ResourceDescription.class);

    when(mappedSampleRD_1.getId()).thenReturn(SAMPLE_RD_ID_1);
    when(mappedSampleRD_2.getId()).thenReturn(SAMPLE_RD_ID_2);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(List.of(mappedSampleRD_1, mappedSampleRD_2, mappedSampleRD_3));
    when(resourceService.getRDById(SAMPLE_RD_ID_1)).thenReturn(sampleRD_1);
    when(resourceService.getRDById(SAMPLE_RD_ID_2)).thenReturn(sampleRD_2);
  }

  private void initRDFromDBWithFileName() throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription rdFromDB = mock(ResourceDescription.class);

    when(rdFromDB.getFileName()).thenReturn("sample_file_name");
    when(resourceService.getRDById(anyString())).thenReturn(rdFromDB);
  }
}