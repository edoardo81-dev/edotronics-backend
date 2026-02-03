package com.example.repository;

import com.example.model.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

	Page<Persona> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

	Page<Persona> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

	Page<Persona> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	Page<Persona> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName,
			Pageable pageable);

	Page<Persona> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCityContainingIgnoreCase(
			String q1, String q2, String q3, String q4, Pageable pageable);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdUserNot(String email, Long idUser);

}
