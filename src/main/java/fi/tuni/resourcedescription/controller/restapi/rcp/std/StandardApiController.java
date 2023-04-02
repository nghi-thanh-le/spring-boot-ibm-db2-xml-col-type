package fi.tuni.resourcedescription.controller.restapi.rcp.std;

import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.model.rcp.standard.Standard;
import fi.tuni.resourcedescription.payload.request.standard.StandardDTO;
import fi.tuni.resourcedescription.service.resource.ResourceCatalogueService;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/standards")
@SecurityRequirement(name = "jwt-api")
public class StandardApiController {
  private final ResourceCatalogueService resourceCatalogueService;
  private final ResourceQueryService resourceQueryService;

  @Autowired
  public StandardApiController(ResourceCatalogueService resourceCatalogueService,
                               ResourceQueryService resourceQueryService) {
    this.resourceCatalogueService = resourceCatalogueService;
    this.resourceQueryService = resourceQueryService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("isAnonymous() or hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<Standard>> getStandards() {
    return ResponseEntity.ok(resourceCatalogueService.getStandards());
  }

  @GetMapping(value = "{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("isAnonymous() or hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<Standard> getStandardById(@PathVariable Integer id) throws ResourceNotFoundException {
    return ResponseEntity.ok(resourceCatalogueService.getStandardById(id));
  }

  @PostMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Standard> saveNewStandard(@RequestBody StandardDTO standard) throws Exception {
    return ResponseEntity.ok(resourceCatalogueService.addNewStandard(standard));
  }

  @PutMapping(value = "{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Standard> updateStandardById(@PathVariable Integer id, @RequestBody StandardDTO standard)
    throws Exception {
    return ResponseEntity.ok(resourceCatalogueService.updateStandardById(id, standard));
  }

  @DeleteMapping(value = "{id}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> deleteStandardById(@PathVariable Integer id) throws Exception {
    boolean result = resourceCatalogueService.deleteStandardById(id);
    if (result) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return ResponseEntity
      .internalServerError()
      .build();
  }

  @GetMapping(value = "{id}/rds")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRDsImplementingStdById(@PathVariable Integer id) throws Exception {
    Standard standard = resourceCatalogueService.getStandardById(id);
    return ResponseEntity.ok(resourceQueryService.getRDsImplementingStdByStdId(standard.getStdId()));
  }
}
