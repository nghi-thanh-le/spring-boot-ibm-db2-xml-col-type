package fi.tuni.resourcedescription.model.rcp.rd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "RDs")
@Setter
@Getter
@NoArgsConstructor
public class ResourceDescription {
  @Id
  private String id;

  @Column
  private String fileName = StringUtils.EMPTY;

  @Column
  @JsonIgnore
  private byte[] content = {};

  @JsonIgnore
  @Column
  private Integer userId;

  @Column
  private String stageId;

  @Column
  private boolean isDeprecated = Boolean.FALSE;
}
