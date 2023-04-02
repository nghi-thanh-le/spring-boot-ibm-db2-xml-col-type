package fi.tuni.resourcedescription.service.user.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Company;
import fi.tuni.resourcedescription.model.user.Role;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.repository.user.CompanyRepository;
import fi.tuni.resourcedescription.repository.user.RoleRepository;
import fi.tuni.resourcedescription.repository.user.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  private static final String SAMPLE_USERNAME = "username";
  private static final String SAMPLE_EMAIL = "username@mail.com";
  private static final Integer SAMPLE_USER_ID = 1;
  private static final Integer[] SAMPLE_ROLE_IDS = new Integer[] {123, 321};

  private static final Integer SAMPLE_COMPANY_ID = 2;

  private static final Integer SAMPLE_ROLE_ID = 3;
  private static final String SAMPLE_ROLE_NAME = "Admin";

  @Mock
  private RoleRepository roleRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  public void test_verify_getting_users_coming_from_repository() {
    userService.getAllUsers();

    verify(userRepository, times(1)).findAll();
  }

  @Test
  public void test_getting_user_by_invalid_id() {
    assertThrows(InternalErrorException.class, () -> userService.getUserById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(SAMPLE_USER_ID));
  }

  @Test
  public void test_getting_user_by_valid_id() throws InternalErrorException, ResourceNotFoundException {
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(mock(User.class)));

    assertNotNull(userService.getUserById(SAMPLE_USER_ID));
  }

  @Test
  public void test_getting_user_by_invalid_username() {
    assertThrows(InternalErrorException.class, () -> userService.getUserByUsername(null));
    assertThrows(InternalErrorException.class, () -> userService.getUserByUsername(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(SAMPLE_USERNAME));
  }

  @Test
  public void test_getting_user_by_valid_username() throws InternalErrorException, ResourceNotFoundException {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mock(User.class)));

    assertNotNull(userService.getUserByUsername(SAMPLE_USERNAME));
  }

  @Test
  public void test_add_new_user_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.addNewUser(null));
    assertThrows(InternalErrorException.class, () -> userService.addNewUser(mock(User.class)));
    assertThrows(InternalErrorException.class, () -> userService.addNewUser(null, SAMPLE_ROLE_IDS));
  }

  @Test
  public void test_add_new_user_with_valid_params() throws InternalErrorException {
    User dataSource = mock(User.class);

    when(dataSource.getCompany()).thenReturn(mock(Company.class));

    userService.addNewUser(dataSource);

    verify(userRepository).save(any(User.class));
  }

  @Test
  public void test_add_new_user_with_role_ids() throws InternalErrorException {
    User dataSource = mock(User.class);

    userService.addNewUser(dataSource, SAMPLE_ROLE_IDS);

    verify(userRepository).save(any(User.class));
  }

  @Test
  public void test_update_user_by_ids_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.updateUserById(null, null));
    assertThrows(InternalErrorException.class, () -> userService.updateUserById(SAMPLE_USER_ID, null));
    assertThrows(InternalErrorException.class, () -> userService.updateUserById(null, mock(User.class)));
    assertThrows(ResourceNotFoundException.class, () -> userService.updateUserById(SAMPLE_USER_ID, mock(User.class)));
  }

  @Test
  public void test_update_user_by_ids_with_valid_params()
    throws InternalErrorException, ResourceNotFoundException {
    User dataSource = mock(User.class);
    Set<Role> dataSourceRoles = Arrays.stream(SAMPLE_ROLE_IDS)
      .map(roleId -> {
        Role role = new Role();
        role.setId(roleId);
        return role;
      })
      .collect(Collectors.toSet());

    when(dataSource.getRoles()).thenReturn(dataSourceRoles);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(mock(User.class)));

    userService.updateUserById(SAMPLE_USER_ID, dataSource);

    verify(userRepository, times(2)).save(any(User.class));
  }

  @Test
  public void test_update_user_and_role_ids_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.updateUser(null, SAMPLE_ROLE_IDS));
    assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(mock(User.class), SAMPLE_ROLE_IDS));
  }

  @Test
  public void test_update_user_and_role_ids_with_valid_params()
    throws InternalErrorException, ResourceNotFoundException {
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(mock(User.class)));

    userService.updateUser(mock(User.class), new Integer[]{});

    verify(userRepository).save(any(User.class));
  }

  @Test
  public void test_delete_user_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.deleteUserById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(SAMPLE_USER_ID));
  }

  @Test
  public void test_delete_user_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(mock(User.class)));

    userService.deleteUserById(SAMPLE_USER_ID);

    verify(userRepository).save(any(User.class));
    verify(userRepository).delete(any(User.class));
  }

  @Test
  public void test_check_user_exist_by_username() {
    assertFalse(userService.existsByUsername(null));
    assertFalse(userService.existsByUsername(StringUtils.EMPTY));

    userService.existsByUsername(SAMPLE_USERNAME);
    verify(userRepository).existsByUsername(SAMPLE_USERNAME);
  }

  @Test
  public void test_check_user_exist_by_email() {
    assertFalse(userService.existsByEmail(null));
    assertFalse(userService.existsByEmail(StringUtils.EMPTY));

    userService.existsByEmail(SAMPLE_EMAIL);
    verify(userRepository).existsByEmail(SAMPLE_EMAIL);
  }

  @Test
  public void test_get_all_companies() {
    userService.getAllCompanies();

    verify(companyRepository).findAll();
  }

  @Test
  public void test_get_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.getCompanyById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.getCompanyById(SAMPLE_COMPANY_ID));
  }

  @Test
  public void test_get_company_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(companyRepository.findById(anyInt())).thenReturn(Optional.of(mock(Company.class)));

    assertNotNull(userService.getCompanyById(SAMPLE_COMPANY_ID));
    verify(companyRepository).findById(SAMPLE_COMPANY_ID);
  }

  @Test
  public void test_add_a_new_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.addNewCompany(null));
  }

  @Test
  public void test_add_a_new_company_by_id_with_valid_params() throws InternalErrorException {
    userService.addNewCompany(mock(Company.class));

    verify(companyRepository).save(any(Company.class));
  }

  @Test
  public void test_update_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.updateCompanyById(null, null));
    assertThrows(InternalErrorException.class, () -> userService.updateCompanyById(SAMPLE_COMPANY_ID, null));
    assertThrows(ResourceNotFoundException.class, () -> userService.updateCompanyById(SAMPLE_COMPANY_ID, mock(Company.class)));
  }

  @Test
  public void test_update_company_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(companyRepository.findById(anyInt())).thenReturn(Optional.of(mock(Company.class)));

    userService.updateCompanyById(SAMPLE_COMPANY_ID, mock(Company.class));

    verify(companyRepository).save(any(Company.class));
  }

  @Test
  public void test_delete_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.deleteCompanyById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.deleteCompanyById(SAMPLE_COMPANY_ID));
  }

  @Test
  public void test_delete_company_by_id_with_valid_params_but_company_still_has_users() {
    User sampleUser = mock(User.class);
    Company sampleUserCompany = mock(Company.class);

    when(sampleUser.getCompany()).thenReturn(sampleUserCompany);
    when(sampleUserCompany.getId()).thenReturn(SAMPLE_COMPANY_ID);

    when(userRepository.findAll()).thenReturn(List.of(sampleUser));

    when(companyRepository.findById(anyInt())).thenReturn(Optional.of(sampleUserCompany));

    assertThrows(InternalErrorException.class, () -> userService.deleteCompanyById(SAMPLE_COMPANY_ID));
  }

  @Test
  public void test_delete_company_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(companyRepository.findById(anyInt())).thenReturn(Optional.of(mock(Company.class)));

    userService.deleteCompanyById(SAMPLE_COMPANY_ID);

    verify(companyRepository).deleteById(anyInt());
  }

  @Test
  public void test_get_all_users_of_a_certain_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.getUsersOfCompanyById(null));
  }

  @Test
  public void test_get_all_users_of_a_certain_company_by_id_with_valid_params() throws InternalErrorException {
    User sampleUser = mock(User.class);
    Company sampleUserCompany = mock(Company.class);

    when(sampleUser.getCompany()).thenReturn(sampleUserCompany);
    when(sampleUserCompany.getId()).thenReturn(SAMPLE_COMPANY_ID);

    when(userRepository.findAll()).thenReturn(List.of(sampleUser));

    assertFalse(userService.getUsersOfCompanyById(SAMPLE_COMPANY_ID).isEmpty());
  }

  @Test
  public void test_remove_user_of_a_certain_company_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.removeUserOfTheCompanyById(null, null));
    assertThrows(InternalErrorException.class, () -> userService.removeUserOfTheCompanyById(SAMPLE_COMPANY_ID, null));
    assertThrows(InternalErrorException.class, () -> userService.removeUserOfTheCompanyById(null, SAMPLE_USER_ID));
    assertThrows(ResourceNotFoundException.class, () -> userService.removeUserOfTheCompanyById(SAMPLE_COMPANY_ID, SAMPLE_USER_ID));
  }

  @Test
  public void test_remove_user_of_a_certain_company_by_id_with_valid_params_but_unmatched_company() {
    User theUser = mock(User.class);
    Company companyOfTheUser = mock(Company.class);

    when(theUser.getCompany()).thenReturn(companyOfTheUser);
    when(companyOfTheUser.getId()).thenReturn(3);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(theUser));

    assertThrows(InternalErrorException.class, () -> userService.removeUserOfTheCompanyById(SAMPLE_COMPANY_ID, SAMPLE_USER_ID));
  }

  @Test
  public void test_remove_user_of_a_certain_company_by_id_with_valid_params_and_matched_company()
    throws InternalErrorException, ResourceNotFoundException {
    User theUser = mock(User.class);
    Company companyOfTheUser = mock(Company.class);

    when(theUser.getCompany()).thenReturn(companyOfTheUser);
    when(companyOfTheUser.getId()).thenReturn(SAMPLE_COMPANY_ID);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(theUser));

    userService.removeUserOfTheCompanyById(SAMPLE_COMPANY_ID, SAMPLE_USER_ID);

    verify(theUser).setCompany(null);
    verify(userRepository).save(theUser);
  }

  @Test
  public void test_get_all_roles() {
    userService.getAllRoles();

    verify(roleRepository).findAll();
  }

  @Test
  public void test_get_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.getRoleById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.getRoleById(SAMPLE_ROLE_ID));
  }

  @Test
  public void test_get_role_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(mock(Role.class)));

    assertNotNull(userService.getRoleById(SAMPLE_ROLE_ID));
    verify(roleRepository).findById(SAMPLE_ROLE_ID);
  }

  @Test
  public void test_getting_role_by_invalid_name() {
    assertThrows(InternalErrorException.class, () -> userService.getRoleByName(null));
    assertThrows(InternalErrorException.class, () -> userService.getRoleByName(StringUtils.EMPTY));
    assertThrows(ResourceNotFoundException.class, () -> userService.getRoleByName(SAMPLE_ROLE_NAME));
  }

  @Test
  public void test_getting_role_by_valid_name() throws InternalErrorException, ResourceNotFoundException {
    when(roleRepository.findByName(anyString())).thenReturn(Optional.of(mock(Role.class)));

    assertNotNull(userService.getRoleByName(SAMPLE_USERNAME));
  }

  @Test
  public void test_users_of_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.getUsersOfRoleById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.getUsersOfRoleById(SAMPLE_ROLE_ID));
  }

  @Test
  public void test_users_of_role_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    Role roleFromDB = mock(Role.class);

    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(roleFromDB));

    userService.getUsersOfRoleById(SAMPLE_ROLE_ID);

    verify(roleFromDB).getUsers();
  }

  @Test
  public void test_add_a_new_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.addNewRole(null));
  }

  @Test
  public void test_add_a_new_role_by_id_with_valid_params() throws InternalErrorException {
    userService.addNewRole(mock(Role.class));

    verify(roleRepository).save(any(Role.class));
  }

  @Test
  public void test_update_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.updateRoleById(null, null));
    assertThrows(InternalErrorException.class, () -> userService.updateRoleById(SAMPLE_ROLE_ID, null));
    assertThrows(ResourceNotFoundException.class, () -> userService.updateRoleById(SAMPLE_ROLE_ID, mock(Role.class)));
  }

  @Test
  public void test_update_role_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(mock(Role.class)));

    userService.updateRoleById(SAMPLE_ROLE_ID, mock(Role.class));

    verify(roleRepository).save(any(Role.class));
  }

  @Test
  public void test_delete_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.deleteRoleById(null));
    assertThrows(ResourceNotFoundException.class, () -> userService.deleteRoleById(SAMPLE_ROLE_ID));
  }

  @Test
  public void test_delete_role_by_id_with_valid_params_but_role_still_has_users() {
    Role sampleRoleFromDB = mock(Role.class);

    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(sampleRoleFromDB));
    when(sampleRoleFromDB.getUsers()).thenReturn(Set.of(mock(User.class)));

    assertThrows(InternalErrorException.class, () -> userService.deleteRoleById(SAMPLE_ROLE_ID));
  }

  @Test
  public void test_delete_role_by_id_with_valid_params() throws InternalErrorException, ResourceNotFoundException {
    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(mock(Role.class)));

    userService.deleteRoleById(SAMPLE_ROLE_ID);

    verify(roleRepository).deleteById(anyInt());
  }

  @Test
  public void test_remove_user_out_of_role_by_id_with_invalid_params() {
    assertThrows(InternalErrorException.class, () -> userService.removeUserOutOfTheRole(null, null));
    assertThrows(InternalErrorException.class, () -> userService.removeUserOutOfTheRole(SAMPLE_ROLE_ID, null));
    assertThrows(InternalErrorException.class, () -> userService.removeUserOutOfTheRole(null, SAMPLE_USER_ID));
    assertThrows(ResourceNotFoundException.class, () -> userService.removeUserOutOfTheRole(SAMPLE_ROLE_ID, SAMPLE_USER_ID));
  }

  @Test
  public void test_remove_user_out_of_role_by_id_with_valid_params_but_unmatched_id() {
    Role roleFromDB = mock(Role.class);
    User userOfThisRole = mock(User.class);

    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(roleFromDB));
    when(roleFromDB.getUsers()).thenReturn(Set.of(userOfThisRole));
    when(userOfThisRole.getId()).thenReturn(456);

    assertThrows(InternalErrorException.class, () -> userService.removeUserOutOfTheRole(SAMPLE_ROLE_ID, SAMPLE_USER_ID));
  }

  @Test
  public void test_remove_user_out_of_role_by_id_with_valid_params_but_matched_id()
    throws InternalErrorException, ResourceNotFoundException {
    Role roleFromDB = mock(Role.class);
    User userOfThisRole = mock(User.class);

    when(roleRepository.findById(anyInt())).thenReturn(Optional.of(roleFromDB));
    when(roleFromDB.getUsers()).thenReturn(Set.of(userOfThisRole));
    when(userOfThisRole.getId()).thenReturn(SAMPLE_USER_ID);

    userService.removeUserOutOfTheRole(SAMPLE_ROLE_ID, SAMPLE_USER_ID);

    verify(userOfThisRole).removeRole(SAMPLE_ROLE_ID);
    verify(userRepository).save(userOfThisRole);
  }
}