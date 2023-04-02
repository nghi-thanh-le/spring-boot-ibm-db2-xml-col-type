package fi.tuni.resourcedescription.repository.standard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.standard.StandardBody;

@Repository
public interface StandardBodyRepository extends JpaRepository<StandardBody, Integer>{
}
