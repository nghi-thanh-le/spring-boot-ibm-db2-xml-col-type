package fi.tuni.resourcedescription.controller.restapi.rcp.capa;

import fi.tuni.resourcedescription.model.rcp.capabilitiy.Capability;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.service.resource.ResourceCatalogueService;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/capabilities")
@SecurityRequirement(name = "jwt-api")
public class CapabilityApiController {
  private final ResourceCatalogueService resourceCatalogueService;
  private final ResourceQueryService resourceQueryService;

  public CapabilityApiController(ResourceCatalogueService resourceCatalogueService,
                                 ResourceQueryService resourceQueryService) {
    this.resourceCatalogueService = resourceCatalogueService;
    this.resourceQueryService = resourceQueryService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  //@PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<Capability>> getCapabilities() {
    return ResponseEntity.ok(resourceCatalogueService.getCapabilities());
  }

  @GetMapping(value = "{name}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  //@PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<Capability>> getCapabilityByName(@PathVariable String name) throws Exception {
    return ResponseEntity.ok(resourceCatalogueService.getCapabilitiesByName(name));
  }

  @GetMapping(value = "{name}/rds",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  //@PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRdImplementingCapabilityById(@PathVariable String name) throws Exception {
    Capability capability = resourceCatalogueService.getCapabilityByName(name);
    return ResponseEntity.ok(resourceQueryService.getRDsImplementingCapabilityByName(capability.getName()));
  }
}
