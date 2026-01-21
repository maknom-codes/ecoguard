package com.maknom.eco.guard.configuration;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfiguration {


   @Bean
   public GraphQLScalarType jsonScalar() {
      return ExtendedScalars.Json;
   }


   @Bean
   public RuntimeWiringConfigurer runtimeWiringConfigurer() {
      return wiringBuilder -> wiringBuilder
              .scalar(ExtendedScalars.Json)
              .build();
   }

}
