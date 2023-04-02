package fi.tuni.resourcedescription.controller.restapi.rcp.category;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@SecurityRequirement(name = "jwt-api")
public class CategoryApiController {
  private final ResourceQueryService resourceQueryService;

  @Autowired
  public CategoryApiController(ResourceQueryService resourceQueryService) {
    this.resourceQueryService = resourceQueryService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<String>> getAllCategories() throws Exception {
    return ResponseEntity.ok(resourceQueryService.getAllCategories());
  }

  @GetMapping(
    value = "/rds",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRDsImplementingCategoryByName(@RequestParam(name = "name") String categoryName) throws Exception {
    return ResponseEntity.ok(resourceQueryService.getRdsByCategoryName(categoryName));
  }
}
