package fi.tuni.resourcedescription.model.rcp.rd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RD_Stages")
@Setter
@Getter
@NoArgsConstructor
public class ResourceStage {
  @Id
  private String id;

  @Column
  private String description;
}
