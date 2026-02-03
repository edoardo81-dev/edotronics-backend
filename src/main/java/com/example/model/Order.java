package com.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOrder;

	@Column(nullable = false)
	private LocalDateTime dateTime;

	@Column(length = 500)
	private String description;

	@Column(nullable = false, length = 60)
	private String shipFirstName;

	@Column(nullable = false, length = 60)
	private String shipLastName;

	@Column(nullable = false, length = 40)
	private String shipPhone;

	@Column(nullable = false, length = 120)
	private String shipAddress;

	@Column(nullable = false, length = 60)
	private String shipCity;

	/* ========= RELAZIONI ========= */
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference("persona-orders")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Persona user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("order-orderProducts")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<OrderProduct> orderProducts;
}
