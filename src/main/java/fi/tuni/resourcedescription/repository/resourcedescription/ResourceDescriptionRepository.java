package fi.tuni.resourcedescription.repository.resourcedescription;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;

@Repository
public interface ResourceDescriptionRepository extends JpaRepository<ResourceDescription, String> {
  Optional<ResourceDescription> findById(String id);
  boolean existsById(String id);

  @Modifying
  @Query(
    value = "update RDs set stageId = :stageId where id= :rdId",
    countQuery = "SELECT count(*) from RDs",
    nativeQuery = true)
  void setRdStage(@Param("stageId") String stageId, @Param("rdId") String rdId);
}
