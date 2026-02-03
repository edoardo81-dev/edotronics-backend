package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false)
	private LocalDateTime startsAt;

	@Column(nullable = false)
	private LocalDateTime endsAt;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private boolean archived = false;

	@OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PromotionProduct> items = new ArrayList<>();
}
