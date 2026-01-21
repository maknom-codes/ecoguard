package com.maknom.eco.guard.configuration;

import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class GraphQLCookieInterceptor implements WebGraphQlInterceptor {


   @Override
   public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
      return chain.next(request).map(response -> {
         var graphQLContext = response.getExecutionInput().getGraphQLContext();

         String accessToken = graphQLContext.get("access_token");
         if (accessToken != null) {
            response.getResponseHeaders().add("Set-Cookie",
                    "access_token=" + accessToken + "; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=900");
         }

         String refreshToken = graphQLContext.get("refresh_token");
         if (refreshToken != null) {
            response.getResponseHeaders().add("Set-Cookie",
                    "refresh_token=" + refreshToken + "; HttpOnly; Secure; SameSite=Lax; Path=/refresh; Max-Age=604800");
         }
         Boolean logout = response.getExecutionInput().getGraphQLContext().get("logout_request");
         if (Boolean.TRUE.equals(logout)) {
            response.getResponseHeaders().add("Set-Cookie", "access_token=; Max-Age=0; Path=/");
            response.getResponseHeaders().add("Set-Cookie", "refresh_token=; Max-Age=0; Path=/refresh");
         }
         return response;
      });
   }
}
