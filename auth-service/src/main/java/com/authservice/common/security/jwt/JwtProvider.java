package com.authservice.common.security.jwt;

import com.authservice.common.enums.Role;
import com.authservice.common.enums.TokenType;
import com.authservice.common.security.session.UserDetailsServiceImpl;
import com.authservice.dto.response.JwtTokenResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${app.jwt.accessTime}")
    private Long accessTokenValidTime;

    @Value("${app.jwt.refreshTime}")
    private Long refreshTokenValidTime;

    @Value("${app.jwt.secret}")
    private String secret;

    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenResponseDto createJwtToken(String email, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        String auth = roles.stream()
                .map(Role::getAuth)
                .collect(Collectors.joining(","));
        claims.put("auth", auth);
        Date now = new Date();

        String accessToken = createAccessToken(claims, now);
        String refreshToken = createRefreshToken(claims, now);
        return new JwtTokenResponseDto(accessToken, refreshToken, refreshTokenValidTime);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;

        } catch (UnsupportedJwtException | MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private String createAccessToken(Claims claims, Date now) {
        claims.put("type", TokenType.ACCESS);
        return createToken(claims, now, accessTokenValidTime);
    }

    private String createRefreshToken(Claims claims, Date now) {
        claims.put("type", TokenType.REFRESH);
        return createToken(claims, now, refreshTokenValidTime);
    }

    private String createToken(Claims claims, Date now, Long validTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}