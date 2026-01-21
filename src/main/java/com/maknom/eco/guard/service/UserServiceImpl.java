package com.maknom.eco.guard.service;

import com.maknom.eco.guard.model.user.TypeRole;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.user.UserRequest;
import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;


   public UserServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
   }


   @Override
   public UserBean create(UserRequest userRequest) {
      UserBean userBean = userRepository.findByEmail(userRequest.getEmail())
              .orElse(null);
      if (userBean == null) {
         userBean = new UserBean();
         userBean.setEmail(userRequest.getEmail());
         userBean.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
         userBean.setName(userRequest.getName());
         userBean.setRole(TypeRole.parseTypeRole(userRequest.getRole()));
         return userRepository.save(userBean);
      }
      return null;
   }


   @Override
   public Optional<UserBean> getUserByEmail(String email) {
      return userRepository.findByEmail(email);
   }

   @Override
   public Optional<UserBean> getUserById(Long id) {
      return userRepository.findById(id);
   }

   @Override
   public UserDetails loadUserByUsername(String username) {
      return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
   }
}
