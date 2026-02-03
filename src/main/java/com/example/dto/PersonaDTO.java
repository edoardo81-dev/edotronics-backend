package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaDTO {

    private Long idUser;

    @NotBlank(message = "firstName obbligatorio")
    @Size(min = 2, max = 50, message = "firstName deve avere 2-50 caratteri")
    private String firstName;

    @NotBlank(message = "lastName obbligatorio")
    @Size(min = 2, max = 50, message = "lastName deve avere 2-50 caratteri")
    private String lastName;

    @NotBlank(message = "email obbligatoria")
    @Email(message = "email non valida")
    private String email;

    @NotBlank(message = "phone obbligatorio")
    @Size(min = 5, max = 20, message = "phone deve avere 5-20 caratteri")
    private String phone;

    @NotBlank(message = "address obbligatorio")
    @Size(min = 5, max = 120, message = "address deve avere 5-120 caratteri")
    private String address;
    
    @NotBlank(message = "city obbligatoria")
    @Size(min = 2, max = 60, message = "city deve avere 2-60 caratteri")
    private String city;

}
