package com.maknom.eco.guard.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class AuthenticationService {

   private static Logger log = LoggerFactory.getLogger(AuthenticationService.class);

   private String ecoSecretKey;

   private long accessTokenExpiration;

   private long refreshTokenExpiration;

   public AuthenticationService(@Value("${spring.application.security.jwt.secret-key}") String ecoSecretKey,
                                @Value("${spring.application.security.jwt.access-token-expiration}") long accessTokenExpiration,
                                @Value("${spring.application.security.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
      this.ecoSecretKey = ecoSecretKey;
      this.accessTokenExpiration = accessTokenExpiration;
      this.refreshTokenExpiration = refreshTokenExpiration;
   }


   public String generateAccessToken(UserDetails userDetails) {
      return generateToken(new HashMap<>(), userDetails, accessTokenExpiration);
   }

   public String generateRefreshToken(UserDetails userDetails) {
      return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
   }

   public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
      log.info("USERNAME: {}", userDetails.getUsername());
      return Jwts.builder()
              .setClaims(extraClaims)
              .subject(userDetails.getUsername())
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(new Date(System.currentTimeMillis() + expiration))
              .signWith(getSignInKey(), SignatureAlgorithm.HS256)
              .compact();
   }

   public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
   }


   public boolean isTokenValid(String token, UserDetails userDetails) {
      final String userName = extractUsername(token);
      return (userName.equals(userDetails.getUsername()))
              && !isTokenExpired(token);
   }

   private boolean isTokenExpired(String token) {
      return extractClaim(token, Claims::getExpiration).before(new Date());
   }

   public Instant getTokenExpired(String token) {
      return extractClaim(token, Claims::getExpiration).toInstant();
   }

   private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
   }

   private Claims extractAllClaims(String token) {
      return Jwts.parser()
              .verifyWith(getSignInKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
   }

   private SecretKey getSignInKey() {
      byte[] keyBytes = Decoders.BASE64.decode(ecoSecretKey);
      return Keys.hmacShaKeyFor(keyBytes);
   }
}
