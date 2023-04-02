package fi.tuni.resourcedescription.controller.restapi.user;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Company;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
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
@RequestMapping("/api/v1/companies")
@SecurityRequirement(name = "jwt-api")
@Hidden
public class CompanyApiController {
  private final UserService userService;

  @Autowired
  public CompanyApiController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<Company>> getCompanies() {
    return ResponseEntity.ok(userService.getAllCompanies());
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Company> createNewCompany(@RequestBody Company company) throws InternalErrorException {
    return ResponseEntity.ok(userService.addNewCompany(company));
  }

  @GetMapping(value = "{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Company> getCompanyById(@PathVariable Integer id)
    throws ResourceNotFoundException, InternalErrorException {
    return ResponseEntity.ok(userService.getCompanyById(id));
  }

  @PutMapping(value = "{id}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Company> modifyCompanyById(@PathVariable Integer id, @RequestBody Company company)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.updateCompanyById(id, company));
  }

  @DeleteMapping(value = "{id}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> deleteCompanyId(@PathVariable Integer id)
    throws InternalErrorException, ResourceNotFoundException {
    userService.deleteCompanyById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "{id}/users",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<User>> getUsersOfACompanyById(@PathVariable Integer id) throws InternalErrorException {
    return ResponseEntity.ok(userService.getUsersOfCompanyById(id));
  }

  @DeleteMapping(value = "{id}/users/{userId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<User>> removeUserOfTheCompanyById(@PathVariable Integer id, @PathVariable Integer userId)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.removeUserOfTheCompanyById(id, userId));
  }
}
