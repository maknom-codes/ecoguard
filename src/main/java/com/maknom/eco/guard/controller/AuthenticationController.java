package com.maknom.eco.guard.controller;

import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.service.AuthenticationService;
import graphql.GraphQLContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;



@Controller
@RequiredArgsConstructor
public class AuthenticationController extends AbstractController {


   private static final String ACCESS_KEY_TOKEN = "access_token";
   private static final String REFRESH_KEY_TOKEN = "refresh_token";

   private static Logger log = LoggerFactory.getLogger(AuthenticationController.class);

   private final AuthenticationManager authenticationManager;

   private final AuthenticationService authenticationService;

   private final UserService userService;



   @MutationMapping
   public AuthResponse login(@Argument String email, @Argument String password, GraphQLContext context) {
      log.info("CONECUEUUIuiUiUUUUUUUUUEUUEUEUEUE");
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password)
      );
      if (!authentication.isAuthenticated()) {
         return new AuthResponse("Invalid Credentials", false, null);
      }
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      UserBean userBean = userService.getUserByEmail(userDetails.getUsername()).get();
      String accessToken = authenticationService.generateAccessToken(userBean);
      String refreshToken = authenticationService.generateRefreshToken(userDetails);
      context.put(ACCESS_KEY_TOKEN, accessToken);
      context.put(REFRESH_KEY_TOKEN, refreshToken);
      return new AuthResponse(
              "Login Success",
              true,
              new UserResponse((
                      userBean.getName()),
                      userBean.getRole().toString(),
                      userBean.getId(),
                      userBean.getUsername())
      );
   }

   @QueryMapping
   public AuthStatus authCheck(Principal principal) {
      try {
         if (principal == null)
            return new AuthStatus("Not authenticated", false);

         UserBean userBean = userService.getUserByEmail(principal.getName()).orElse(null);
         if (userBean == null || !userBean.isEnabled()) {
            return new AuthStatus("User account disabled or deleted", false);
         }
         return new AuthStatus("Login successful", true);
      } catch (Exception e) {
         log.warn("Auth check failed: {}", e.getMessage());
         return new AuthStatus("Authentication failed: " + e.getMessage(), false);
      }
   }

   @QueryMapping
   public UserResponse userProfile(Authentication authentication) {
      if (authentication == null || !authentication.isAuthenticated()) {
         throw new RuntimeException("Authentication required");
      }

      String username = authentication.getName();
      UserBean userBean =userService.getUserByEmail(username).get();
      return new UserResponse(userBean.getName(), userBean.getRole().toString(), userBean.getId(), userBean.getEmail());
   }

   @MutationMapping
   public AuthResponse refreshToken(
           GraphQLContext context, HttpServletRequest request) {

      try {
         String refreshToken = getCookieValue(request, "refresh_token");
         String username = authenticationService.extractUsername(refreshToken);
         UserDetails userDetails = userService.loadUserByUsername(username);
         UserBean userBean = userService.getUserByEmail(username).get();
         String newAccessToken = authenticationService.generateAccessToken(userDetails);

         context.put(ACCESS_KEY_TOKEN, newAccessToken);
         return new AuthResponse(
                 "Login Successful, Token has been renewed",
                 true,
                 new UserResponse((
                         userBean.getName()),
                         userBean.getRole().toString(),
                         userBean.getId(),
                         userBean.getUsername())
         );
      } catch (Exception e) {
         log.error("Token refresh failed: {}", e.getMessage());
         return new AuthResponse("Invalid refresh token", false, null);
      }
   }

   @MutationMapping
   public AuthResponse logout(GraphQLContext context) {
      context.put("logout_request", true);
      return new AuthResponse(
              "Logout Successful",
              false,
              null
      );
   }
}
