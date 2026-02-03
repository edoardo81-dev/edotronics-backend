package com.example.controller;

import com.example.dto.MeProfileDTO;
import com.example.dto.UpdateMeProfileRequest;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.model.AuthUser;
import com.example.model.Persona;
import com.example.repository.AuthUserRepository;
import com.example.repository.PersonaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/profile")
@RequiredArgsConstructor
public class MeProfileController {

    private final AuthUserRepository authUserRepo;
    private final PersonaRepository personaRepo;

    @GetMapping
    public MeProfileDTO me(Authentication authentication) {
        AuthUser authUser = resolveAuthUser(authentication);

        if (authUser.getPersona() == null) {
            throw new BadRequestException("Nessuna persona collegata a questo account");
        }

        Persona p = authUser.getPersona();
        return toDto(authUser, p);
    }

    @PutMapping
    public MeProfileDTO update(@Valid @RequestBody UpdateMeProfileRequest req, Authentication authentication) {
        AuthUser authUser = resolveAuthUser(authentication);

        if (authUser.getPersona() == null || authUser.getPersona().getIdUser() == null) {
            throw new BadRequestException("Nessuna persona collegata a questo account");
        }

        Persona p = authUser.getPersona();

        String email = safeTrim(req.getEmail());
        String phone = safeTrim(req.getPhone());
        String address = safeTrim(req.getAddress());
        String city = safeTrim(req.getCity());

        if (email == null || phone == null || address == null || city == null) {
            throw new BadRequestException("Dati profilo non validi");
        }

        if (personaRepo.existsByEmailIgnoreCaseAndIdUserNot(email, p.getIdUser())) {
            throw new ConflictException("Email già in uso");
        }

        p.setEmail(email);
        p.setPhone(phone);
        p.setAddress(address);
        p.setCity(city);

        Persona saved = personaRepo.save(p);
        return toDto(authUser, saved);
    }

    private AuthUser resolveAuthUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Utente non autenticato");
        }

        return authUserRepo.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Utente non valido"));
    }

    private MeProfileDTO toDto(AuthUser u, Persona p) {
        return new MeProfileDTO(
                u.getUsername(),
                u.getRole() != null ? u.getRole().name() : null,
                p.getFirstName(),
                p.getLastName(),
                p.getEmail(),
                p.getPhone(),
                p.getAddress(),
                p.getCity()
        );
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
