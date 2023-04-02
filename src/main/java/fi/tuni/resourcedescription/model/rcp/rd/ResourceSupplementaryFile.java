package fi.tuni.resourcedescription.model.rcp.rd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Blob;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RD_Supplementary_Files")
@Getter
@Setter
@NoArgsConstructor
public class ResourceSupplementaryFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Integer id;
  @Column
  private String fileName;
  @Column
  @Lob
  @JsonIgnore
  private Blob content;
  @Column
  @JsonIgnore
  private String resourceId;
}
