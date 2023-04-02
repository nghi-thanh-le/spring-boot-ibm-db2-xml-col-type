package fi.tuni.resourcedescription.model.rcp.rd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RD_Validation_Results")
@Getter
@Setter
@NoArgsConstructor
public class ResourceValidationResult {
  @Id
  @Column
  private String id;
  @Column
  private String state;
  @Column
  private String validationMessage;
}
