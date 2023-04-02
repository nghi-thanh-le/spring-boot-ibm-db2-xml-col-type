package fi.tuni.resourcedescription.controller.restapi.rcp.review;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceReview;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewApiController {
  private final ResourceService resourceService;

  @Autowired
  public ReviewApiController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @GetMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<List<ResourceReview>> getAllReviews() {
    return ResponseEntity.ok(resourceService.getAllReviews());
  }
}
