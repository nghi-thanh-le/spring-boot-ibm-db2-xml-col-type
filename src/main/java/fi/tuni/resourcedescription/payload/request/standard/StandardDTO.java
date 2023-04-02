package fi.tuni.resourcedescription.payload.request.standard;

import lombok.Data;

@Data
public class StandardDTO {
  private String stdId;
  private String code;
  private String name;
  private String description;
  private String url;
  private StandardBodyDTO Body;
}
