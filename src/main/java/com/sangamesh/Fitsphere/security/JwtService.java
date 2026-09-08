package com.sangamesh.Fitsphere.security;


import com.sangamesh.Fitsphere.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;



    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("type",TokenType.ACCESS.name());
        claims.put("role", user.getRole().name());
        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("type",TokenType.REFRESH.name());
        claims.put("role", user.getRole().name());
        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractTokenType(String token){
        return extractClaim(token,claims -> claims.get("type",String.class));
    }
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean hasTokenType(String token, TokenType type){
        return extractTokenType(token).equals(type.name());
    }

    public boolean isTokenValid(String token, String userId) {
        return extractSubject(token).equals(userId) && hasTokenType(token,TokenType.ACCESS)&&!isTokenExpired(token)
                && !isTokenExpired(token);
    }

    public LocalDateTime getRefreshTokenExpiry() {
        return LocalDateTime.now().plus(java.time.Duration.ofMillis(refreshExpiration));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
