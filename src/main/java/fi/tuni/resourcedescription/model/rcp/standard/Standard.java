package fi.tuni.resourcedescription.model.rcp.standard;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "Standards")
@Accessors(chain=true)
@NoArgsConstructor
@AllArgsConstructor
public class Standard {

  @Id
  @Getter
  @Setter
  private Integer id;

  @Column
  @Getter
  @Setter
  private String stdId;

  @Column(name = "stdCode")
  @Getter
  @Setter
  private String code;

  @Column(name = "stdName")
  @Getter
  @Setter
  private String name;

  @Column(name = "stdDescription")
  @Getter
  @Setter
  private String description;

  @Column(name = "stdUrl")
  @Getter
  @Setter
  private String url;

//  @Column(name = "nbrARDs")
//  @Getter
//  private Integer nbrARDs = 0;

  @Column(name = "nbrRDs")
  @Getter
  private Integer nbrRDs = 0;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "bodyId", referencedColumnName = "id")
  @Getter
  @Setter
  private StandardBody Body;
}
