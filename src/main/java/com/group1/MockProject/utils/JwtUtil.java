package com.group1.MockProject.utils;

import com.group1.MockProject.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;// Import Enum UserRole
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Random;

@Component
public class JwtUtil {

  // Secret key nên được lấy từ một nơi bảo mật, ví dụ: từ application.properties, môi trường, hoặc
  // vault
  private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  private static final long EXPIRATION_TIME = 86400000; // 1 day

  // Phương thức tạo JWT token, thêm cả vai trò vào payload
  public static String generateToken(User user) {
    return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("role", user.getRole().name())
            .claim("role", user.getRole())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY)
            .compact();
  }


  public static Claims decodeToken(String token) {
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
  }

  public static boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      throw new BadCredentialsException("Token đã hết hạn hoặc không hợp lệ");
    }
  }



      public static String extractEmail (String token){
        try {
          return Jwts.parserBuilder()
                  .setSigningKey(SECRET_KEY)
                  .build()
                  .parseClaimsJws(token)
                  .getBody()
                  .getSubject();
        } catch (Exception e) {
          throw new BadCredentialsException("Token đã hết hạn hoặc không hợp lệ");
        }
      }


  public static String generateOTP() {
    StringBuilder otp = new StringBuilder();
    Random random = new Random();
    int count = 0;
    while (count < 6) {
      otp.append(random.nextInt(10));
      count++;
    }
    return otp.toString();
  }


}