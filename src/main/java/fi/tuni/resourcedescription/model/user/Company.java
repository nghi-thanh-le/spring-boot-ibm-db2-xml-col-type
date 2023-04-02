package fi.tuni.resourcedescription.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column
  private String name;
  @Column
  private String url;
  @Column
  private String email;
  @Column
  private String address;
  @Column
  private String postalCode;
  @Column
  private String postOffice;
  @Column
  private String country;
  @Column
  private String contactPerson;
  @Column
  private String phone;
}
