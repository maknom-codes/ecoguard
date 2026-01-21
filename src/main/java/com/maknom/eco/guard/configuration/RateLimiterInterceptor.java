package com.maknom.eco.guard.configuration;

import com.maknom.eco.guard.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

   private static final String X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";

   private static final String X_FORWARDED_FOR = "X-Forwarded-For";

   private final RateLimiterService rateLimiterService;

   public RateLimiterInterceptor(RateLimiterService rateLimiterService) {
      this.rateLimiterService = rateLimiterService;
   }

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

      String remoteAddr = request.getHeader(X_FORWARDED_FOR);
      if(remoteAddr == null || remoteAddr.isEmpty()) {
         remoteAddr = request.getRemoteAddr();
      }
      String apiUniqueKey = remoteAddr.split(",")[0];
      Bucket bucket = rateLimiterService.resolveBucket(apiUniqueKey);

      if (bucket.tryConsume(1)){
         response.addHeader(X_RATE_LIMIT_REMAINING, String.valueOf(bucket.getAvailableTokens()));
         return true;
      } else {
         response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
         response.getWriter().write("TOO MANY REQUESTS: PLEASE WAIT FOR A MINUTE...");
         return false;
      }
   }
}
