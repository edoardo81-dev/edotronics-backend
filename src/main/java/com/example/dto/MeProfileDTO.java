package com.example.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeProfileDTO {
    private String username;
    private String role;

    private String firstName;
    private String lastName;

    private String email;
    private String phone;
    private String address;
    private String city;
}
