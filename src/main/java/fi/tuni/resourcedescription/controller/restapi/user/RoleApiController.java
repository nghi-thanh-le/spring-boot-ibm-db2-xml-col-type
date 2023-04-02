package fi.tuni.resourcedescription.controller.restapi.user;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Role;
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
@RequestMapping("/api/v1/roles")
@SecurityRequirement(name = "jwt-api")
@Hidden
public class RoleApiController {
  private final UserService userService;

  @Autowired
  public RoleApiController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<Role>> getRoles() {
    return ResponseEntity.ok(userService.getAllRoles());
  }

  @GetMapping(value = "{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Role> getRoleById(@PathVariable Integer id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.getRoleById(id));
  }

  @GetMapping(value = "{id}/users",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<User>> getUsersOfRoleById(@PathVariable Integer id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.getUsersOfRoleById(id));
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Role> addNewRole(@RequestBody Role role) throws InternalErrorException {
    return ResponseEntity.ok(userService.addNewRole(role));
  }

  @PutMapping(value = "{id}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Role> updateRoleById(@PathVariable Integer id, @RequestBody Role role)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.updateRoleById(id, role));
  }

  @DeleteMapping(value = "{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> deleteRoleById(@PathVariable Integer id)
    throws InternalErrorException, ResourceNotFoundException {
    userService.deleteRoleById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "{id}/users/{userId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<User>> removeUserOutOfTheRole(@PathVariable Integer id, @PathVariable Integer userId)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.removeUserOutOfTheRole(id, userId));
  }
}
