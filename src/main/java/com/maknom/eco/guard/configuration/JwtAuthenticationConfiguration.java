package com.maknom.eco.guard.configuration;

import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationConfiguration extends OncePerRequestFilter {

   private final AuthenticationService authenticationService;

   private final UserService userService;

   private String extractTokenFromCookie(HttpServletRequest request) {
      if (request.getCookies() == null) {
         return null;
      }
      return Arrays.stream(request.getCookies())
              .filter(cookie -> "access_token".equals(cookie.getName()))
              .map(Cookie::getValue)
              .findFirst()
              .orElse(null);
   }

   @Override
   protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) throws ServletException, IOException {


      final String token = extractTokenFromCookie(request);

      if (token != null) {
         try {
            String username = authenticationService.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               if (authenticationService.isTokenValid(token, userDetails)) {
                  UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                          username,
                          null,
                          userDetails.getAuthorities()
                  );
                  authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                  SecurityContextHolder.getContext().setAuthentication(authToken);
               }else {
                  SecurityContextHolder.clearContext();
               }
            }
         } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
         }
      }
      filterChain.doFilter(request, response);
   }
}
