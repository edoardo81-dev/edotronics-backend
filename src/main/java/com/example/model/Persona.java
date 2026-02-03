package com.example.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = true)
	private Integer age;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, length = 60)
	private String city;

	@Column(nullable = false)
	private boolean active;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference("persona-orders")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Order> orders = new ArrayList<>();
}
