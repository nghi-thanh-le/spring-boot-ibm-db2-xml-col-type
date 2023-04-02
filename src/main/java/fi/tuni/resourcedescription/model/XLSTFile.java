package fi.tuni.resourcedescription.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@NoArgsConstructor
@Data
public class XLSTFile {
  private String name;
  @JsonIgnore
  private Resource resource;
}
