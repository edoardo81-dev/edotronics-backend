package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.dto.RegisterRequest;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.model.AuthUser;
import com.example.model.Persona;
import com.example.model.Role;
import com.example.repository.AuthUserRepository;
import com.example.repository.PersonaRepository;
import com.example.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserRepository authUserRepo;
    private final PersonaRepository personaRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {

        String username = safeTrim(req.getUsername());
        if (username == null) throw new BadRequestException("Credenziali non valide");

        AuthUser user = authUserRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BadRequestException("Credenziali non valide"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Credenziali non valide");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name());
    }

    @PostMapping("/register")
    @Transactional
    public LoginResponse register(@Valid @RequestBody RegisterRequest req) {

        String username = safeTrim(req.getUsername());
        String password = req.getPassword(); // non trim
        String firstName = safeTrim(req.getFirstName());
        String lastName = safeTrim(req.getLastName());
        String email = safeTrim(req.getEmail());
        String phone = safeTrim(req.getPhone());
        String address = safeTrim(req.getAddress());
        String city = safeTrim(req.getCity());

        if (username == null || password == null || password.isBlank()) {
            throw new BadRequestException("Dati registrazione non validi");
        }

        if (authUserRepo.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username già in uso");
        }

        if (email != null && personaRepo.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email già in uso");
        }

        Persona p = new Persona();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setEmail(email);
        p.setPhone(phone);
        p.setAddress(address);
        p.setCity(city);
        p.setActive(true);
        p.setAge(null);

        Persona savedPersona = personaRepo.save(p);

        AuthUser u = new AuthUser();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(Role.USER);
        u.setPersona(savedPersona);

        authUserRepo.save(u);

        String token = jwtService.generateToken(username, Role.USER.name());
        return new LoginResponse(token, Role.USER.name());
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
