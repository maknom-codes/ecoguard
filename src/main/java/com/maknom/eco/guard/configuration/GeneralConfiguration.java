package com.maknom.eco.guard.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.maknom.eco.guard.model.user.UserService;
import org.locationtech.jts.geom.Polygon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.time.Duration;


@Configuration
public class GeneralConfiguration implements WebMvcConfigurer {

   private final UserService userService;

   private final RateLimiterInterceptor rateLimiterInterceptor;




   public GeneralConfiguration(UserService userService,
                               RateLimiterInterceptor rateLimiterInterceptor
                               ) {
      this.userService = userService;
      this.rateLimiterInterceptor = rateLimiterInterceptor;
   }

   @Bean
   public ServerStartupLogger serverStartupLogger() {
      return new ServerStartupLogger();
   }


   @Bean
   public RedisCacheManager cacheManager (RedisConnectionFactory redisConnectionFactory) {

      RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofMinutes(10))
              .disableCachingNullValues()
              .serializeValuesWith(RedisSerializationContext.SerializationPair
                      .fromSerializer(new GenericJackson2JsonRedisSerializer()));

      return RedisCacheManager.builder(redisConnectionFactory)
              .cacheDefaults(cacheConfiguration)
              .build();

   }

   @Bean
   public ObjectMapper objectMapper () {
      ObjectMapper objectMapper = new ObjectMapper();
      SimpleModule simpleModule = new SimpleModule();
      simpleModule.addSerializer(Polygon.class, new GeoJsonPolygonSerializer());
      objectMapper.registerModule(simpleModule);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      return objectMapper;
   }

   @Bean
   public AuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
      daoAuthenticationProvider.setUserDetailsService(userService);
      daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
      return daoAuthenticationProvider;
   }

   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
      return authenticationConfiguration.getAuthenticationManager();
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(rateLimiterInterceptor)
              .addPathPatterns("/graphql")
              .addPathPatterns("/graphiql");
   }

}
