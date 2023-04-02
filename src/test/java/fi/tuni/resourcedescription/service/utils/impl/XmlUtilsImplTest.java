package fi.tuni.resourcedescription.service.utils.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
public class XmlUtilsImplTest {
  private static final String SAMPLE_XML_STRING = "<root>lorem-ipsum</root>";
  private static final String SAMPLE_XLS_PATH = "classpath:/path/to/some/where";
  private static final String SAMPLE_XLS_CONTENT = "<xsl:template match=\"//me:ResourceDescription\"></xsl:template>";

  @Mock
  private ResourceLoader resourceLoader;

  @InjectMocks
  public XmlUtilsImpl xmlUtils;

  @Test
  public void test_xml_string_to_json_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringToJsonString(null));
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringToJsonString(StringUtils.EMPTY));
  }

  @Test
  public void test_xml_string_to_json_with_valid_params() throws InternalErrorException {
    assertEquals(xmlUtils.xmlStringToJsonString(SAMPLE_XML_STRING), "\"lorem-ipsum\"");
  }

  @Test
  public void test_transform_xml_string_with_xls_via_invalid_parameters() {
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringViaXsl((String) null, SAMPLE_XLS_PATH));
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringViaXsl(StringUtils.EMPTY, SAMPLE_XLS_PATH));
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringViaXsl((byte[]) null, SAMPLE_XLS_PATH));
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringViaXsl(SAMPLE_XML_STRING, null));
    assertThrows(InternalErrorException.class, () -> xmlUtils.xmlStringViaXsl(SAMPLE_XML_STRING, StringUtils.EMPTY));
  }

  @Test
  @Disabled("It is hard to write this test case due to complicated input stream.")
  public void test_transform_xml_string_with_xls_via_valid_parameters() throws Exception {
    Resource xslFile = mock(Resource.class);

    when(resourceLoader.getResource(SAMPLE_XLS_PATH)).thenReturn(xslFile);
//    when(xslFile.getInputStream()).thenReturn()

    xmlUtils.xmlStringViaXsl(SAMPLE_XML_STRING, SAMPLE_XLS_PATH);
  }
}