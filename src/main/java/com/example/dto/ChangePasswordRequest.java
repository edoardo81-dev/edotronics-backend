package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "currentPassword obbligatoria")
    private String currentPassword;

    @NotBlank(message = "newPassword obbligatoria")
    @Size(min = 4, max = 100, message = "newPassword deve avere almeno 4 caratteri")
    private String newPassword;

    @NotBlank(message = "confirmNewPassword obbligatoria")
    private String confirmNewPassword;
}
