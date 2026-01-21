package com.maknom.eco.guard.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityAuthenticationManager {

   private final JwtAuthenticationConfiguration jwtAuthenticationConfiguration;

   private final AuthenticationProvider authenticationProvider;

   public SecurityAuthenticationManager(JwtAuthenticationConfiguration jwtAuthenticationConfiguration,
                                        AuthenticationProvider authenticationProvider) {
      this.jwtAuthenticationConfiguration = jwtAuthenticationConfiguration;
      this.authenticationProvider = authenticationProvider;
   }


   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
      httpSecurity
              .cors(Customizer.withDefaults())
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/graphql", "/graphiql").permitAll()
                      .requestMatchers("/health", "/actuator/health").permitAll()
                      .anyRequest().authenticated())
              .authenticationProvider(authenticationProvider)
              .addFilterBefore(jwtAuthenticationConfiguration, UsernamePasswordAuthenticationFilter.class);
      return httpSecurity.build();
   }


}
