package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;
    
    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

}
