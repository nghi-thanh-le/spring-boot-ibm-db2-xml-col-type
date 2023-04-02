package fi.tuni.resourcedescription.repository.user;

import fi.tuni.resourcedescription.model.user.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(String name);
//  Optional<Role> findByName(RoleConstants name);
}
