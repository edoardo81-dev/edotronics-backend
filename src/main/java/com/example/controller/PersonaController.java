package com.example.controller;

import com.example.dto.PersonaDTO;
import com.example.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public Page<PersonaDTO> getAll(@PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        return personaService.getAll(pageable);
    }

    @GetMapping("/id/{id}")
    public PersonaDTO getById(@PathVariable Long id) {
        return personaService.getById(id);
    }

    @PostMapping
    public PersonaDTO create(@Valid @RequestBody PersonaDTO dto) {
        return personaService.create(dto);
    }

    @PutMapping("/{id}")
    public PersonaDTO update(@PathVariable Long id, @Valid @RequestBody PersonaDTO dto) {
        return personaService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        personaService.delete(id);
    }

    @GetMapping("/cognome/{last}")
    public Page<PersonaDTO> byLastName(
            @PathVariable String last,
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable
    ) {
        return personaService.byLastName(last, pageable);
    }

    @GetMapping("/search")
    public Page<PersonaDTO> search(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable
    ) {
        return personaService.search(firstName, lastName, email, pageable);
    }

    @GetMapping("/searchq")
    public Page<PersonaDTO> searchQ(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable
    ) {
        return personaService.searchQ(q, pageable);
    }
}
