package com.vonage.CustomerSupportTicketingSystem.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 1000*60*60;
    private static final String SECRET_KEY="1234567890123456789012345678901234567890";

    private final Key key= Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String email,String role) {
        try {
            return Jwts.builder().
                    setSubject(email).
                    claim("role", role).
                    setIssuedAt(new Date()).
                    setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }catch (Exception e){
            System.out.println("Error generating token: " + e.getMessage());
            return null;
        }
    }
    public String  extractEmail(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }catch (Exception e){
            System.out.println("error extracting email: "+ e.getMessage());
            return null;
        }
    }
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            System.out.println("Invalid Jwt token: "+ e.getMessage());
            return false;
        }
    }
    public String extractRole(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("Role",String.class);
        }catch (Exception e){
            System.out.println("error extracting role: "+ e.getMessage());
            return null;
        }
    }
}



