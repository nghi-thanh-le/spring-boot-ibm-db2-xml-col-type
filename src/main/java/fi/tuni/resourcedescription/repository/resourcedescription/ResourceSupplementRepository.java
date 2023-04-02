package fi.tuni.resourcedescription.repository.resourcedescription;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceSupplementaryFile;

@Repository
public interface ResourceSupplementRepository extends JpaRepository<ResourceSupplementaryFile, Integer> {
  Optional<List<ResourceSupplementaryFile>> findAllByResourceId(String resourceId);
  Optional<ResourceSupplementaryFile> findByIdAndResourceId(Integer id, String resourceId);
  void deleteByIdAndResourceId(Integer id, String resourceId);
}
