package fi.tuni.resourcedescription.repository.resourcedescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceStage;

@Repository
public interface ResourceStageRepository extends JpaRepository<ResourceStage, String> {
}
