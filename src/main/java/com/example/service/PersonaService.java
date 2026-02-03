package com.example.service;

import com.example.dto.PersonaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonaService {

    Page<PersonaDTO> getAll(Pageable pageable);

    PersonaDTO getById(Long id);

    PersonaDTO create(PersonaDTO dto);

    PersonaDTO update(Long id, PersonaDTO dto);

    void delete(Long id);

    Page<PersonaDTO> byLastName(String lastName, Pageable pageable);

    Page<PersonaDTO> search(String firstName, String lastName, String email, Pageable pageable);

    Page<PersonaDTO> searchQ(String q, Pageable pageable);
}
