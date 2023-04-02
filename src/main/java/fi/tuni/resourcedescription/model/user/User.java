package fi.tuni.resourcedescription.model.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "Users")
@Accessors(chain=true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private static final long serialVersionUID = 1L;

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  @Getter
  @Setter
  private String username = StringUtils.EMPTY;

  @Column
  @Getter
  @Setter
  private String password = StringUtils.EMPTY;

  @Column(name = "firstname")
  @Getter
  @Setter
  private String firstName = StringUtils.EMPTY;

  @Column(name = "lastname")
  @Getter
  @Setter
  private String lastName = StringUtils.EMPTY;

  @Column(name = "email")
  @Getter
  @Setter
  private String email = StringUtils.EMPTY;

  @Column(name = "phone")
  @Getter
  @Setter
  private String phone = StringUtils.EMPTY;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "companyID", referencedColumnName = "id")
  @Getter
  @Setter
  private Company company;

  @ManyToMany(fetch = FetchType.LAZY,
    cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
    })
  @JoinTable(
    name = "Join_User_Role",
    joinColumns = {@JoinColumn(name = "userID")},
    inverseJoinColumns = {@JoinColumn(name = "roleID")}
  )
  @Getter
  Set<Role> roles = new HashSet<>();

  public void addRole(Role role) {
    this.roles.add(role);
    role.getUsers().add(this);
  }

  public void removeRole(Integer roleId) {
    Role role = this.roles.stream().filter(r -> r.getId().equals(roleId)).findFirst().orElse(null);
    if (Objects.nonNull(role)) {
      this.roles.remove(role);
      role.getUsers().remove(this);
    }
  }
}
