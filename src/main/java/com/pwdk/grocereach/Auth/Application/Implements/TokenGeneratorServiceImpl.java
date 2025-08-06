package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.TokenGeneratorService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.TokenType;
import com.pwdk.grocereach.Auth.Domain.ValueOfObject.Token;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenGeneratorServiceImpl implements TokenGeneratorService {

    private final JwtEncoder accessTokenEncoder;
    private final JwtEncoder refreshTokenEncoder;

    public TokenGeneratorServiceImpl(@Qualifier("jwtEncoder") JwtEncoder accessTokenEncoder,
                                     @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder) {
        this.accessTokenEncoder = accessTokenEncoder;
        this.refreshTokenEncoder = refreshTokenEncoder;
    }

    @Override
    public Token generateAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(15, ChronoUnit.MINUTES);

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("grocereach-api")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("kind", TokenType.ACCESS.getType())
                .claim("scope", scope)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        String tokenValue = accessTokenEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        return new Token(tokenValue, (int) (expiresAt.getEpochSecond() - now.getEpochSecond()), "Bearer");
    }

    @Override
    public Token generateRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(30, ChronoUnit.DAYS);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("grocereach-api")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("kind", TokenType.REFRESH.getType())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        String tokenValue = refreshTokenEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        return new Token(tokenValue, (int) (expiresAt.getEpochSecond() - now.getEpochSecond()), "Bearer");
    }
}
