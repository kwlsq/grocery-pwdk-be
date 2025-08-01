package com.pwdk.grocereach.Auth.Domain.ValueOfObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String value;
    private int expiresAt;
    @Builder.Default
    private String tokenType = "Bearer";


}