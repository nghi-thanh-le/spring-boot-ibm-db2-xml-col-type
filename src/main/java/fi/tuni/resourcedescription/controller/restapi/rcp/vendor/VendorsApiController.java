package fi.tuni.resourcedescription.controller.restapi.rcp.vendor;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vendors")
@SecurityRequirement(name = "jwt-api")
public class VendorsApiController {
  private final ResourceQueryService resourceQueryService;

  @Autowired
  public VendorsApiController(ResourceQueryService resourceQueryService) {
    this.resourceQueryService = resourceQueryService;
  }

  @GetMapping(
    value = "{vendorName}/rds"
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRDsByVendorName(@PathVariable String vendorName) throws
    InternalErrorException {
    return ResponseEntity.ok(resourceQueryService.getRdsByVendorName(vendorName));
  }
}
