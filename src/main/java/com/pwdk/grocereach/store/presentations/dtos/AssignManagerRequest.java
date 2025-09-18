package com.pwdk.grocereach.store.presentations.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AssignManagerRequest {
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
}