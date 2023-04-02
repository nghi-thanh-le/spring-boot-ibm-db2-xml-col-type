package fi.tuni.resourcedescription.service.security.impl;

import fi.tuni.resourcedescription.model.user.User;
import fi.tuni.resourcedescription.payload.SimpleUserDetails;
import fi.tuni.resourcedescription.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimpleUserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  @Autowired
  public SimpleUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    return SimpleUserDetails.build(user);
  }
}
