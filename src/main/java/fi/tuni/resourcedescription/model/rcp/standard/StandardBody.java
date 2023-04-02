package fi.tuni.resourcedescription.model.rcp.standard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Standard_Bodies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardBody {
  @Id
  @Getter
  @Setter
  private Integer id;
  @Column
  private String bodyId;
  @Column(name = "bodyName")
  private String name;
  @Column(name = "bodyUrl")
  private String url;
  @Column(name = "bodyDescription")
  private String description;
}
