package com.maknom.eco.guard.integration;

import com.maknom.eco.guard.PostgresContainerConfig;
import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.PolygonGeometry;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.user.UserRequest;
import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.model.zone.ProtectedZoneRequest;
import com.maknom.eco.guard.model.zone.ProtectedZoneService;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import com.maknom.eco.guard.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Transactional
@Sql(scripts = "/init-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CreateUserIntegrationTest extends PostgresContainerConfig {

   @Autowired
   private UserService userService;

   @Autowired
   private UserRepository userRepository;



   @Test
   void createUser_thenItShouldSuccess() {

      UserRequest request = new UserRequest();
      request.setEmail("kouakou.ko@gmail.com");
      request.setName("Kouakou");
      request.setRole("admin");
      request.setPassword("abc");

      UserBean userBean = userService.create(request);

      assertNotNull(userBean);
      assertNotNull(userBean.getId());
      assertEquals("Kouakou", userBean.getName());
      assertEquals("kouakou.ko@gmail.com", userBean.getEmail());
   }
}
