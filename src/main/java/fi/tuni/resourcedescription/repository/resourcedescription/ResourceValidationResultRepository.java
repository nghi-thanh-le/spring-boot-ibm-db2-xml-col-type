package fi.tuni.resourcedescription.repository.resourcedescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;

@Repository
public interface ResourceValidationResultRepository extends JpaRepository<ResourceValidationResult, String> {
}
