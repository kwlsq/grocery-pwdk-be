package com.pwdk.grocereach.Auth.Infrastructure.Securities;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfigProperties {
    private String secret;
    private String refreshSecret;
}
