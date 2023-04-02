package fi.tuni.resourcedescription.service.utils.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ByteArrayTransformationServiceImplTest {
  private static final String SAMPLE_STRING = "<foo>bar</foo>";

  private ByteArrayTransformationService byteArrayTransformationService;

  @BeforeEach
  protected void init() {
    byteArrayTransformationService = new ByteArrayTransformationServiceImpl();
  }

  @Test
  public void test_transform_an_empty_array_return_empty_string() {
    assertEquals(byteArrayTransformationService.transformBytesArray(null, Boolean.TRUE), StringUtils.EMPTY);
    assertEquals(byteArrayTransformationService.transformBytesArray(new byte[] {}, Boolean.FALSE), StringUtils.EMPTY);
  }

  @Test
  public void test_transform_a_string() {
    assertEquals(byteArrayTransformationService.transformBytesArray(SAMPLE_STRING.getBytes(), Boolean.FALSE), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo>bar</foo>");
    assertEquals(byteArrayTransformationService.transformBytesArray(SAMPLE_STRING.getBytes(), Boolean.TRUE), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo>bar</foo>\n");
  }
}