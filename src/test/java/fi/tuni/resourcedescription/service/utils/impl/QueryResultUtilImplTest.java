package fi.tuni.resourcedescription.service.utils.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.tuni.resourcedescription.service.utils.QueryResultUtil;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryResultUtilImplTest {
  private QueryResultUtil queryResultUtil;

  @BeforeEach
  protected void init() {
    queryResultUtil = new QueryResultUtilImpl();
  }

  @Test
  public void test_query_invalid_parameters() throws Exception {
    assertTrue(queryResultUtil.build((String) null).getResult().isEmpty());
    assertTrue(queryResultUtil.build(StringUtils.EMPTY).getResult().isEmpty());
  }

  @Test
  public void test_query_with_valid_parameter() throws Exception {
    assertFalse(queryResultUtil.build("sample_result_string").getResult().isEmpty());
  }

  @Test
  public void test_query_list_results_invalid_parameters() throws Exception {
    assertTrue(queryResultUtil.build((List<String>) null).getResult().isEmpty());
    assertTrue(queryResultUtil.build(Collections.emptyList()).getResult().isEmpty());
  }

  @Test
  public void test_query_list_results_with_valid_parameter() throws Exception {
    assertFalse(queryResultUtil.build(List.of("sample_result_string_1", "sample_result_string_2")).getResult().isEmpty());
  }
}