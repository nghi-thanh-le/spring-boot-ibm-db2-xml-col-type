package fi.tuni.resourcedescription.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public class StringRowMapper implements RowMapper<String> {
  private static final Logger log = LoggerFactory.getLogger(StringRowMapper.class);
  private static final int SQL_XML_RESULT_COL_INDEX = 1;

  @Override
  public String mapRow(ResultSet resultSet, int rowNum) {
    String result = StringUtils.EMPTY;
    try {
      SQLXML xml = resultSet.getSQLXML(SQL_XML_RESULT_COL_INDEX);
      result = xml.getString();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
    return result;
  }
}
