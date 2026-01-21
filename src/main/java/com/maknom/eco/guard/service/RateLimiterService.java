package com.maknom.eco.guard.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class RateLimiterService {

   private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();

   public Bucket resolveBucket (String key) {
      return bucketMap.computeIfAbsent(key, k-> createNewBucket());
   }

   private Bucket createNewBucket() {
      Refill refill = Refill.greedy(10, Duration.ofMinutes(1));
      Bandwidth limit = Bandwidth.classic(10, refill);
      return Bucket.builder().addLimit(limit).build();
   }


}
