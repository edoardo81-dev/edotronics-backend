package com.example.controller;

import com.example.dto.OrderDTO;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.model.AuthUser;
import com.example.repository.AuthUserRepository;
import com.example.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/orders")
@RequiredArgsConstructor
public class MeOrderController {

    private final OrderService orderService;
    private final AuthUserRepository authUserRepo;

    @GetMapping
    public Page<OrderDTO> myOrders(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "dateTime") Pageable pageable
    ) {
        Long personaId = resolvePersonaId(authentication);
        return orderService.getByPersona(personaId, pageable);
    }

    @GetMapping("/{idOrder}")
    public OrderDTO myOrderById(@PathVariable Long idOrder, Authentication authentication) {
        Long personaId = resolvePersonaId(authentication);

        OrderDTO dto = orderService.getById(idOrder);
        if (dto.getIdUser() == null || !dto.getIdUser().equals(personaId)) {
            throw new NotFoundException("Ordine non trovato: " + idOrder);
        }
        return dto;
    }

    @PostMapping
    public OrderDTO createMyOrder(@Valid @RequestBody OrderDTO dto, Authentication authentication) {
        Long personaId = resolvePersonaId(authentication);
        return orderService.createForPersona(personaId, dto);
    }

    private Long resolvePersonaId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Utente non autenticato");
        }

        AuthUser authUser = authUserRepo.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Utente non valido"));

        if (authUser.getPersona() == null || authUser.getPersona().getIdUser() == null) {
            throw new BadRequestException("Nessuna persona collegata a questo account");
        }

        return authUser.getPersona().getIdUser();
    }
}
