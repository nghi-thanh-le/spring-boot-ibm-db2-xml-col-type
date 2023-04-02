package fi.tuni.resourcedescription.controller.restapi.rcp.eclass;

import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/eclasses")
@SecurityRequirement(name = "jwt-api")
public class EClassesApiController {
  private final ResourceQueryService resourceQueryService;

  @Autowired
  public EClassesApiController(ResourceQueryService resourceQueryService) {
    this.resourceQueryService = resourceQueryService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_XML_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getEClasses() throws Exception {
    return ResponseEntity.ok(resourceQueryService.getAllEClasses().getResult());
  }

  @GetMapping(
    value = "{id}",
    produces = MediaType.APPLICATION_XML_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getEClassById(@PathVariable String id) throws Exception {
    return ResponseEntity.ok(resourceQueryService.getEClassesById(id).getResult());
  }

  @GetMapping(
    value = "{id}/rds"
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRDsImplementingEClassesById(@PathVariable String id) throws Exception {
    return ResponseEntity.ok(resourceQueryService.getRDsImplementingEClassesById(id));
  }
}
