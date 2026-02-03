package com.example.controller;

import com.example.dto.ChangePasswordRequest;
import com.example.exception.BadRequestException;
import com.example.model.AuthUser;
import com.example.repository.AuthUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me/password")
@RequiredArgsConstructor
public class MePasswordController {

    private final AuthUserRepository authUserRepo;
    private final BCryptPasswordEncoder encoder;

    @PostMapping
    public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                             Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Utente non autenticato");
        }

        AuthUser user = authUserRepo.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Utente non valido"));

        String current = req.getCurrentPassword();
        String newPass = req.getNewPassword();
        String confirm = req.getConfirmNewPassword();

        if (!newPass.equals(confirm)) {
            throw new BadRequestException("Le nuove password non coincidono");
        }

        if (!encoder.matches(current, user.getPasswordHash())) {
            throw new BadRequestException("Password attuale non corretta");
        }

        if (encoder.matches(newPass, user.getPasswordHash())) {
            throw new BadRequestException("La nuova password deve essere diversa da quella attuale");
        }

        user.setPasswordHash(encoder.encode(newPass));
        authUserRepo.save(user);

        return Map.of("message", "Password aggiornata");
    }
}
