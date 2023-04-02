package fi.tuni.resourcedescription.payload.request.standard;

import lombok.Data;
@Data
public class StandardBodyDTO {
  private String bodyId;
  private String name;
  private String url;
  private String description;
}
