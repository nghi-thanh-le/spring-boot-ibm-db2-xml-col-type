package fi.tuni.resourcedescription.controller.restapi.user;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Role;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Set;
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
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "jwt-api")
@Hidden
public class UserApiController {
  private final UserService userService;

  @Autowired
  public UserApiController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping(value = "/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<User> getUserById(@PathVariable Integer id)
    throws ResourceNotFoundException, InternalErrorException {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<User> createNewUser(@RequestBody User user)
    throws InternalErrorException {
    return ResponseEntity.ok(userService.addNewUser(user));
  }

  @PutMapping(value = "/{id}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<User> updateUserById(@PathVariable Integer id, @RequestBody User user)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(userService.updateUserById(id, user));
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> deleteUserById(@PathVariable Integer id)
    throws InternalErrorException, ResourceNotFoundException {
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{id}/roles",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<Set<Role>> getRolesOfUserById(@PathVariable Integer id)
    throws ResourceNotFoundException, InternalErrorException {
    return ResponseEntity.ok(userService.getUserById(id).getRoles());
  }
}
