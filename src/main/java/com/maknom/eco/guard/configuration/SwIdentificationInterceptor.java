package com.maknom.eco.guard.configuration;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;


@Component
public class SwIdentificationInterceptor implements WebGraphQlInterceptor {

   @Override
   public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
      String swHeader = request.getHeaders().getFirst("x-Source-SW");
      if (swHeader == null) {
         request.configureExecutionInput((executionInput, builder) ->
                 builder.graphQLContext(Collections.singletonMap("X-Sync-Source", "NOT_SET")).build());

      } else {
         request.configureExecutionInput(((executionInput, builder) -> builder
                 .graphQLContext(Collections.singletonMap("X-Sync-Source", swHeader))
                 .build()));
      }
      return chain.next(request);
   }
}
