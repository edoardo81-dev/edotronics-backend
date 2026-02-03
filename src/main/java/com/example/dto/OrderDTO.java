package com.example.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "idOrder", "dateTime", "idUser", "firstName", "lastName", "address", "city", "phone",
		"description", "products" })
public class OrderDTO {

	private Long idOrder;

	private String dateTime;

	private Long idUser;

	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String phone;

	@Size(max = 200, message = "descrizione max 200 caratteri")
	private String description;

	@NotEmpty(message = "prodotti deve contenere almeno 1 prodotto")
	@Valid
	private List<OrderItemDTO> products;
}
