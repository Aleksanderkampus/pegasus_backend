package com.pegasus.application.security.services;

import com.pegasus.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pegasus.application.models.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

    return UserDetailsImpl.build(user);
  }

  public User getCurrentUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthorizationServiceException("No User currently logged while executing this operation");
    }

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    User user = new User(userDetails.getEmail(), userDetails.getPassword(), userDetails.getFirstname(), userDetails.getLastname(), userDetails.getWeight());
    user.setId(userDetails.getId());
    return user;
  }

}
