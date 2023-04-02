package fi.tuni.resourcedescription.service.user;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.user.Company;
import fi.tuni.resourcedescription.model.user.Role;
import fi.tuni.resourcedescription.model.user.User;
import java.util.List;

public interface UserService {
// Users
  List<User> getAllUsers();
  User getUserById(Integer id) throws ResourceNotFoundException, InternalErrorException;
  User getUserByUsername(String username) throws ResourceNotFoundException, InternalErrorException;
  User addNewUser(User newUser) throws InternalErrorException;
  User addNewUser(User newUser, Integer[] roleIds) throws InternalErrorException;
  User updateUserById(Integer id, User user) throws ResourceNotFoundException, InternalErrorException;
  User updateUser(User userToUpdate, Integer[] roleIds) throws InternalErrorException, ResourceNotFoundException;
  void deleteUserById(Integer id) throws ResourceNotFoundException, InternalErrorException;
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);

//  Companies
  List<Company> getAllCompanies();
  Company getCompanyById(Integer id) throws ResourceNotFoundException, InternalErrorException;
  Company addNewCompany(Company company) throws InternalErrorException;
  Company updateCompanyById(Integer id, Company company) throws ResourceNotFoundException, InternalErrorException;
  void deleteCompanyById(Integer id) throws ResourceNotFoundException, InternalErrorException;
  List<User> getUsersOfCompanyById(Integer id) throws InternalErrorException;
  List<User> removeUserOfTheCompanyById(Integer companyId, Integer userId)
    throws InternalErrorException, ResourceNotFoundException;

  //  Roles
  List<Role> getAllRoles();
  Role getRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException;
  Role getRoleByName(String name) throws InternalErrorException, ResourceNotFoundException;
  List<User> getUsersOfRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException;
  Role addNewRole(Role role) throws InternalErrorException;
  Role updateRoleById(Integer id, Role role) throws InternalErrorException, ResourceNotFoundException;
  void deleteRoleById(Integer id) throws InternalErrorException, ResourceNotFoundException;
  List<User> removeUserOutOfTheRole(Integer roleId, Integer userId)
    throws InternalErrorException, ResourceNotFoundException;
}
