package com.epam.finaltask.service;

import com.epam.finaltask.dto.AuthRequestDTO;
import com.epam.finaltask.dto.AuthResponseDTO;
import com.epam.finaltask.dto.RefreshRequestDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenValidityMillis;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenValidityMillis;

    private Map<String, String> refreshTokenStore;

    private Set<String> tokenBlacklist;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserDetailsService userDetailsService,
                                     PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        refreshTokenStore = new HashMap<>();
        tokenBlacklist = new HashSet<>();
    }

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) {
        String username = authRequestDTO.getUsername();
        String rawPassword = authRequestDTO.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = generateToken(username, accessTokenValidityMillis);
        String refreshToken = generateToken(username, refreshTokenValidityMillis);

        refreshTokenStore.put(username, refreshToken);
        return new AuthResponseDTO(accessToken, refreshToken, username);
    }

    @Override
    public AuthResponseDTO refreshToken(RefreshRequestDTO refreshRequestDTO) {
        String providedRefreshToken = refreshRequestDTO.getRefreshToken();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(providedRefreshToken)
                    .getBody();
            String username = claims.getSubject();

            String storedRefreshToken = refreshTokenStore.get(username);
            if (storedRefreshToken == null || !storedRefreshToken.equals(providedRefreshToken)) {
                throw new BadCredentialsException("Refresh token is invalid or expired");
            }

            String newAccessToken = generateToken(username, accessTokenValidityMillis);
            String newRefreshToken = generateToken(username, refreshTokenValidityMillis);

            refreshTokenStore.put(username, newRefreshToken);
            return new AuthResponseDTO(newAccessToken, newRefreshToken, username);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token", e);
        }
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.add(token);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            refreshTokenStore.remove(username);
        } catch (Exception e) {
        }
    }

    private String generateToken(String username, long validityMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityMillis);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}