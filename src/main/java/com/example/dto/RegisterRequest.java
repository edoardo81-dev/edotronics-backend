package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "username obbligatorio")
    @Size(min = 3, max = 50, message = "username deve avere 3-50 caratteri")
    private String username;

    @NotBlank(message = "password obbligatoria")
    @Size(min = 4, max = 100, message = "password deve avere almeno 4 caratteri")
    private String password;

    @NotBlank(message = "nome obbligatorio")
    @Size(min = 2, max = 50, message = "nome deve avere 2-50 caratteri")
    private String firstName;

    @NotBlank(message = "cognome obbligatorio")
    @Size(min = 2, max = 50, message = "cognome deve avere 2-50 caratteri")
    private String lastName;

    @NotBlank(message = "email obbligatoria")
    @Email(message = "email non valida")
    private String email;

    @NotBlank(message = "telefono obbligatorio")
    @Size(min = 5, max = 20, message = "telefono deve avere 5-20 caratteri")
    private String phone;

    @NotBlank(message = "indirizzo e numero civico obbligatorio")
    @Size(min = 5, max = 120, message = "indirizzo deve avere 5-120 caratteri")
    private String address;

    @NotBlank(message = "città obbligatoria")
    @Size(min = 2, max = 60, message = "città deve avere 2-60 caratteri")
    private String city;
}

