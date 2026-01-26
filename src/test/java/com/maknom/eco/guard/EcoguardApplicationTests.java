package com.maknom.eco.guard;

import com.maknom.eco.guard.controller.AuthenticationController;
import com.maknom.eco.guard.controller.EcoGuardController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class EcoguardApplicationTests extends PostgresContainerConfig {

    @Autowired
    private EcoGuardController ecoGuardController;
    @Autowired
    private AuthenticationController authenticationController;

    @Test
    void contextLoads () {

        Assertions.assertNotNull(authenticationController);
        Assertions.assertNotNull(ecoGuardController);
    }
}
