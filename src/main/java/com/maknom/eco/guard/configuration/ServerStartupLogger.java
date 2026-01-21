package com.maknom.eco.guard.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


public class ServerStartupLogger {

   private static final Logger log = LoggerFactory.getLogger(ServerStartupLogger.class);

   @EventListener(ApplicationReadyEvent.class)
   public void whenReady() {
      log.info("ECO GUARD is Ready !");
   }

}
