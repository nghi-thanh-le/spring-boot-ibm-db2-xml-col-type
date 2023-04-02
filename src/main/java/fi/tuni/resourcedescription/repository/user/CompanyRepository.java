package fi.tuni.resourcedescription.repository.user;

import fi.tuni.resourcedescription.model.user.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
