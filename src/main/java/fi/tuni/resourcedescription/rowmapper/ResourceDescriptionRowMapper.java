package fi.tuni.resourcedescription.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;

public class ResourceDescriptionRowMapper implements RowMapper<ResourceDescription> {
  private static final String FILE_NAME_COL_LABEL = "fileName";
  private static final String ID_COL_LABEL = "id";

  @Override
  public ResourceDescription mapRow(ResultSet rs, int rowNum) throws SQLException {
    ResourceDescription resourceDescription = new ResourceDescription();
    resourceDescription.setFileName(rs.getString(FILE_NAME_COL_LABEL));
    resourceDescription.setId(rs.getString(ID_COL_LABEL));
    return resourceDescription;
  }
}
