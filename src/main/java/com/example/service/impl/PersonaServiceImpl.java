package com.example.service.impl;

import com.example.dto.PersonaDTO;
import com.example.exception.NotFoundException;
import com.example.mapper.DtoMapper;
import com.example.model.Persona;
import com.example.repository.PersonaRepository;
import com.example.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> getAll(Pageable pageable) {
        return personaRepo.findAll(pageable).map(DtoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO getById(Long id) {
        Persona p = personaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona non trovata: " + id));
        return DtoMapper.toDto(p);
    }

    @Override
    public PersonaDTO create(PersonaDTO dto) {
        normalize(dto);
        Persona saved = personaRepo.save(DtoMapper.toEntity(dto));
        return DtoMapper.toDto(saved);
    }

    @Override
    public PersonaDTO update(Long id, PersonaDTO dto) {
        normalize(dto);

        Persona p = personaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona non trovata: " + id));

        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getPhone());
        p.setAddress(dto.getAddress());
        p.setCity(dto.getCity());

        return DtoMapper.toDto(personaRepo.save(p));
    }

    @Override
    public void delete(Long id) {
        if (!personaRepo.existsById(id)) {
            throw new NotFoundException("Persona non trovata: " + id);
        }
        personaRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> byLastName(String lastName, Pageable pageable) {
        String ln = normalize(lastName);
        return personaRepo.findByLastNameContainingIgnoreCase(ln == null ? "" : ln, pageable)
                .map(DtoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> search(String firstName, String lastName, String email, Pageable pageable) {
        String fn = normalize(firstName);
        String ln = normalize(lastName);
        String em = normalize(email);

        if (fn != null && ln != null) {
            return personaRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(fn, ln, pageable)
                    .map(DtoMapper::toDto);
        }
        if (fn != null) return personaRepo.findByFirstNameContainingIgnoreCase(fn, pageable).map(DtoMapper::toDto);
        if (ln != null) return personaRepo.findByLastNameContainingIgnoreCase(ln, pageable).map(DtoMapper::toDto);
        if (em != null) return personaRepo.findByEmailContainingIgnoreCase(em, pageable).map(DtoMapper::toDto);

        return getAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> searchQ(String q, Pageable pageable) {
        String qq = normalize(q);
        if (qq == null) return getAll(pageable);

        return personaRepo
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCityContainingIgnoreCase(
                        qq, qq, qq, qq, pageable
                )
                .map(DtoMapper::toDto);
    }

    private void normalize(PersonaDTO dto) {
        if (dto == null) return;
        if (dto.getFirstName() != null) dto.setFirstName(dto.getFirstName().trim());
        if (dto.getLastName() != null) dto.setLastName(dto.getLastName().trim());
        if (dto.getEmail() != null) dto.setEmail(dto.getEmail().trim());
        if (dto.getPhone() != null) dto.setPhone(dto.getPhone().trim());
        if (dto.getAddress() != null) dto.setAddress(dto.getAddress().trim());
        if (dto.getCity() != null) dto.setCity(dto.getCity().trim());
    }

    private String normalize(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
