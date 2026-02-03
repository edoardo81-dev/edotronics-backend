package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "username obbligatorio")
    private String username;

    @NotBlank(message = "password obbligatoria")
    private String password;
}
