package fi.tuni.resourcedescription.repository.resourcedescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceReview;

@Repository
public interface ResourceReviewRepository extends JpaRepository<ResourceReview, String> {
}
