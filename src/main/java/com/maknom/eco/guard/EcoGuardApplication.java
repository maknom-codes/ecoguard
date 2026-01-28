package com.maknom.eco.guard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(exclude = {    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@EnableJpaRepositories(basePackages = "com.maknom.eco.guard.repository")
@EntityScan(basePackages = "com.maknom.eco.guard.model")
@EnableCaching
public class EcoGuardApplication {
   public static void main(String[] args) {
      SpringApplication.run(EcoGuardApplication.class, args);
   }

}
