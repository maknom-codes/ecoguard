package com.maknom.eco.guard.model.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

   UserBean create(UserRequest userRequest);

   Optional<UserBean> getUserByEmail(String email);

   Optional<UserBean> getUserById(Long id);


   UserDetails loadUserByUsername(String username);
}
