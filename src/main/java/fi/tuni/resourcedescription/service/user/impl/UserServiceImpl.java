package fi.tuni.resourcedescription.service.user.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Company;
import fi.tuni.resourcedescription.model.user.Role;
import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.repository.user.CompanyRepository;
import fi.tuni.resourcedescription.repository.user.RoleRepository;
import fi.tuni.resourcedescription.repository.user.UserRepository;
import fi.tuni.resourcedescription.service.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(RoleRepository roleRepository,
                         UserRepository userRepository,
                         CompanyRepository companyRepository,
                         PasswordEncoder passwordEncoder) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.passwordEncoder = passwordEncoder;
  }

  //  Users
  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User getUserById(Integer id) throws ResourceNotFoundException, InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("User with null id does not exist.");
    }

    return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found User with id " + id));
  }

  @Override
  public User getUserByUsername(String username) throws ResourceNotFoundException, InternalErrorException {
    if (StringUtils.isBlank(username)) {
      throw new InternalErrorException("User with null username does not exist.");
    }

    return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Not found User with name " + username));
  }

  @Override
  public User addNewUser(User user) throws InternalErrorException {
    if (Objects.isNull(user) || Objects.isNull(user.getCompany())) {
      throw new InternalErrorException("Null user to save or user is missing a company");
    }

    Integer[] roleIdsOfNewUser = extractRoleIdsFromUser(user);
    return addNewUser(user, roleIdsOfNewUser);
  }

  @Override
  public User addNewUser(User newUser, Integer[] roleIds) throws InternalErrorException {
    if (Objects.isNull(newUser)) {
      throw new InternalErrorException("Can't save a null object!");
    }

    User userToSave = initUserFromDataSource(newUser);
    populateRoleIdsToTheUser(roleIds, userToSave);

    try {
      return userRepository.save(userToSave);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public User updateUserById(Integer id, User dataSource) throws ResourceNotFoundException, InternalErrorException {
    if (Objects.isNull(dataSource)) {
      throw new InternalErrorException("Cannot update existing user by an null user.");
    }

    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot update user with a null id.");
    }

    Integer[] roleIds = extractRoleIdsFromUser(dataSource);
    User userToModified = getUserById(id);
    return updateUser(userToModified, dataSource, roleIds);
  }

  @Override
  public User updateUser(User dataSource, Integer[] roleIds) throws InternalErrorException,
    ResourceNotFoundException {
    if (Objects.isNull(dataSource)) {
      throw new InternalErrorException("Cannot update existing user by an null user.");
    }

    User theUserFromDB = getUserById(dataSource.getId());
    return updateUser(theUserFromDB, dataSource, roleIds);
  }

  @Override
  public void deleteUserById(Integer id) throws ResourceNotFoundException, InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Null user to delete.");
    }

    User userToDel = getUserById(id);
    userToDel.setCompany(null);

    Set<Role> oldRoles = new HashSet<>(userToDel.getRoles());
    for (Role role : oldRoles) {
      userToDel.removeRole(role.getId());
    }

    try {
      userRepository.save(userToDel);
      userRepository.delete(userToDel);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public boolean existsByUsername(String username) {
    if (StringUtils.isBlank(username)) {
      return Boolean.FALSE;
    }

    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    if (StringUtils.isBlank(email)) {
      return Boolean.FALSE;
    }

    return userRepository.existsByEmail(email);
  }

  // Companies
  @Override
  public List<Company> getAllCompanies() {
    return companyRepository.findAll();
  }

  @Override
  public Company getCompanyById(Integer id) throws ResourceNotFoundException, InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot find company with null id.");
    }

    return companyRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot find company with id " + id));
  }

  @Override
  public Company addNewCompany(Company company) throws InternalErrorException {
    if (Objects.isNull(company)) {
      throw new InternalErrorException("Null company to save");
    }

    Company companyToSave = new Company();
    saveCompanyProps(companyToSave, company);

    try {
      return companyRepository.save(companyToSave);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public Company updateCompanyById(Integer id, Company company) throws ResourceNotFoundException,
    InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot update company with a null id.");
    }

    if (Objects.isNull(company)) {
      throw new InternalErrorException("Invalid company with a null datasource.");
    }

    Company companyToSave = getCompanyById(id);
    saveCompanyProps(companyToSave, company);

    try {
      return companyRepository.save(companyToSave);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public void deleteCompanyById(Integer id) throws ResourceNotFoundException, InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot delete company by a null id.");
    }

    Company companyToDel = getCompanyById(id);
    List<User> usersOfCurrentCompany = getUsersOfCompanyById(id);

    if (!usersOfCurrentCompany.isEmpty()) {
      throw new InternalErrorException("There are still " + usersOfCurrentCompany.size() + " belonging to this company.");
    }

    try {
      companyRepository.deleteById(companyToDel.getId());
    } catch (Exception e) {
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<User> getUsersOfCompanyById(Integer id) throws InternalErrorException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot retrieve users of a company by a null id.");
    }

    return getAllUsers()
      .stream()
      .filter(user -> Objects.nonNull(user.getCompany()) && Objects.nonNull(user.getCompany().getId()))
      .filter(user -> user.getCompany().getId().equals(id))
      .collect(Collectors.toList());
  }

  @Override
  public List<User> removeUserOfTheCompanyById(Integer companyId, Integer userId)
    throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(userId) || Objects.isNull(companyId)) {
      throw new InternalErrorException("Invalid userId " + userId + " or companyId " + companyId);
    }

    User theUserToModify = getUserById(userId);

    if ((Objects.isNull(theUserToModify.getCompany()) || Objects.isNull(theUserToModify.getCompany().getId())) ||
      !theUserToModify.getCompany().getId().equals(companyId)) {
      throw new InternalErrorException("User does not have company or company id does not match");
    }

    try {
      theUserToModify.setCompany(null);
      userRepository.save(theUserToModify);
      return getUsersOfCompanyById(companyId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

//  Roles
  @Override
  public List<Role> getAllRoles() {
  return roleRepository.findAll();
}

  @Override
  public Role getRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot find role by null id.");
    }

    return roleRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot find role with id " + id));
  }

  @Override
  public Role getRoleByName(String name) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(name)) {
      throw new InternalErrorException("Null role name to find!");
    }
    return roleRepository.findByName(name)
      .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
  }

  @Override
  public List<User> getUsersOfRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Cannot get users of a role by null id.");
    }

    Role theRole = getRoleById(id);
    return new ArrayList<>(theRole.getUsers());
  }

  @Override
  public Role addNewRole(Role role) throws InternalErrorException {
    if (Objects.isNull(role)) {
      throw new InternalErrorException("Can not save new role as role to save is null");
    }

    Role roleToSave = new Role();
    roleToSave.setId(roleRepository.findAll().size() + 1);
    roleToSave.setName(role.getName());
    roleToSave.setDescription(role.getDescription());

    try {
      return roleRepository.save(roleToSave);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public Role updateRoleById(Integer id, Role role) throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id) || Objects.isNull(role)) {
      throw new InternalErrorException("Invalid roleId " + id + " or roleToUpdate is " + role);
    }

    Role roleToSave = getRoleById(id);
    roleToSave.setName(role.getName());
    roleToSave.setDescription(role.getDescription());

    try {
      return roleRepository.save(roleToSave);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public void deleteRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(id)) {
      throw new InternalErrorException("Can not delete role by a null id.");
    }

    Role roleToDel = getRoleById(id);
    if (!roleToDel.getUsers().isEmpty()) {
      log.error("still users binding to this role");
      throw new InternalErrorException("There are still users binding to this role.");
    }

    try {
      roleRepository.deleteById(roleToDel.getId());
    } catch (Exception e) {
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<User> removeUserOutOfTheRole(Integer roleId, Integer userId)
    throws InternalErrorException, ResourceNotFoundException {
    if (Objects.isNull(roleId) || Objects.isNull(userId)) {
      throw new InternalErrorException("Invalid roleId " + roleId + " or invalid userId " + userId);
    }

    Role roleToModify = getRoleById(roleId);
    User theUserToModify = roleToModify
      .getUsers()
      .stream()
      .filter(Objects::nonNull)
      .filter(user -> Objects.nonNull(user.getId()) && user.getId().equals(userId))
      .findFirst()
      .orElse(null);

    if (Objects.isNull(theUserToModify)) {
      throw new InternalErrorException("User does not belong to this role");
    }

    try {
      theUserToModify.removeRole(roleId);
      userRepository.save(theUserToModify);
      return new ArrayList<>(getRoleById(roleId).getUsers());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  private User initUserFromDataSource(User dataSource) {
    User user = new User();
    user.setUsername(dataSource.getUsername());
    user.setFirstName(dataSource.getFirstName());
    user.setLastName(dataSource.getLastName());
    user.setPhone(dataSource.getPhone());
    user.setEmail(dataSource.getEmail());
    user.setPassword(passwordEncoder.encode(dataSource.getPassword()));

    if (Objects.nonNull(dataSource.getCompany())) {
      companyRepository
        .findById(dataSource.getCompany().getId())
        .ifPresent(dataSource::setCompany);
    }

    return user;
  }

  private void populateRoleIdsToTheUser(Integer[] roleIds, User theUser) {
    if (ArrayUtils.isNotEmpty(roleIds)) {
      Arrays.stream(roleIds)
        .map(roleRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(theUser::addRole);
    }
  }

  private User updateUser(User userFromDB, User dataSource, Integer[] roleIds) throws InternalErrorException {
    userFromDB.setFirstName(dataSource.getFirstName());
    userFromDB.setLastName(dataSource.getLastName());
    userFromDB.setPhone(dataSource.getPhone());
    userFromDB.setEmail(dataSource.getEmail());

    if (Objects.nonNull(dataSource.getCompany())) {
      userFromDB.setCompany(dataSource.getCompany());
    }

    try {
      if (ArrayUtils.isNotEmpty(roleIds)) {
        userFromDB
          .getRoles()
          .stream()
          .map(Role::getId)
          .collect(Collectors.toList())
          .forEach(userFromDB::removeRole);
        userRepository.save(userFromDB);
        populateRoleIdsToTheUser(roleIds, userFromDB);
      }

      return userRepository.save(userFromDB);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  private Integer[] extractRoleIdsFromUser(User theUser) {
    Integer[] roleIdsOfTheUser = new Integer[] {};

    if (Objects.nonNull(theUser.getRoles()) && !theUser.getRoles().isEmpty()) {
      roleIdsOfTheUser = theUser
        .getRoles()
        .stream()
        .map(Role::getId)
        .toArray(Integer[]::new);
    }

    return roleIdsOfTheUser;
  }

  private void saveCompanyProps(Company companyToSave, Company sourceCompany) {
    companyToSave.setName(StringUtils.defaultIfBlank(sourceCompany.getName(), StringUtils.EMPTY));
    companyToSave.setUrl(StringUtils.defaultIfBlank(sourceCompany.getUrl(), StringUtils.EMPTY));
    companyToSave.setEmail(StringUtils.defaultIfBlank(sourceCompany.getEmail(), StringUtils.EMPTY));
    companyToSave.setAddress(StringUtils.defaultIfBlank(sourceCompany.getAddress(), StringUtils.EMPTY));
    companyToSave.setPostalCode(StringUtils.defaultIfBlank(sourceCompany.getPostalCode(), StringUtils.EMPTY));
    companyToSave.setPostOffice(StringUtils.defaultIfBlank(sourceCompany.getPostOffice(), StringUtils.EMPTY));
    companyToSave.setCountry(StringUtils.defaultIfBlank(sourceCompany.getCountry(), StringUtils.EMPTY));
    companyToSave.setContactPerson(StringUtils.defaultIfBlank(sourceCompany.getContactPerson(), StringUtils.EMPTY));
    companyToSave.setPhone(StringUtils.defaultIfBlank(sourceCompany.getPhone(), StringUtils.EMPTY));
  }
}
