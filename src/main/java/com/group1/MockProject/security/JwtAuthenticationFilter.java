package com.group1.MockProject.security;

import com.group1.MockProject.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private UserDetailsService userDetailsService;

  @Autowired
  public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getRequestURI().startsWith("/login/oauth2/code/")
        || request.getRequestURI().startsWith("/oauth2/authorize")
        || request.getRequestURI().matches("^/api/v1/auth/.*")) {
      filterChain.doFilter(request, response); // Bypass for OAuth2
      return;
    }

    String token = getTokenFromRequest(request);

    if (StringUtils.hasText(token) && JwtUtil.validateToken(token)) {

      String email = JwtUtil.extractEmail(token);

      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      System.out.println("VALID TOKEN: " + token);
      System.out.println("USER EMAIL: " + userDetails.getUsername());
      System.out.println("USER ROLE: " + userDetails.getAuthorities());
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      System.out.println("AUTHENTICATION TOKEN: " + authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null
        && bearerToken.startsWith("Bearer ")
        && StringUtils.hasText(bearerToken)) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
