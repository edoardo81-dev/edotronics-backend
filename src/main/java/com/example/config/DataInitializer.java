package com.example.config;

import com.example.model.*;
import com.example.repository.AuthUserRepository;
import com.example.repository.PersonaRepository;
import com.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initDatabase(PersonaRepository userRepository, ProductRepository productRepository,
			AuthUserRepository authUserRepository, BCryptPasswordEncoder encoder) {

		return args -> {

			if (userRepository.count() > 0 || productRepository.count() > 0 || authUserRepository.count() > 0) {
				return;
			}

			LocalDateTime now = LocalDateTime.now();

			/* ===================== PERSONE ===================== */
			Persona u1 = new Persona();
			u1.setFirstName("Mario");
			u1.setLastName("Rossi");
			u1.setAge(30);
			u1.setEmail("mario@example.com");
			u1.setPhone("3331234567");
			u1.setAddress("Via Gallia 10");
			u1.setCity("Roma");
			u1.setActive(true);
			u1.setOrders(new ArrayList<>());

			Persona u2 = new Persona();
			u2.setFirstName("Luisa");
			u2.setLastName("Verdi");
			u2.setAge(null);
			u2.setEmail("luisa@example.com");
			u2.setPhone("3479876543");
			u2.setAddress("Corso Italia 25");
			u2.setCity("Milano");
			u2.setActive(true);
			u2.setOrders(new ArrayList<>());

			List<Persona> savedPeople = userRepository.saveAll(List.of(u1, u2));
			Persona u1Saved = savedPeople.get(0);
			Persona u2Saved = savedPeople.get(1);

			/* ===================== AUTH USERS (LOGIN) ===================== */
			AuthUser admin = new AuthUser(null, "admin", encoder.encode("admin"), Role.ADMIN, null);

			AuthUser user = new AuthUser(null, "user", encoder.encode("user"), Role.USER, u1Saved);

			authUserRepository.saveAll(List.of(admin, user));

			/* ===================== PRODUCTS ===================== */
			List<Product> products = new ArrayList<>();

			// ACCESSORI
			products.add(prod("Mouse Logitech", 29.99, 50, "/images/products/mouse-logitech.jpg",
					ProductCategory.ACCESSORI));
			products.add(prod("Tastiera Meccanica", 79.90, 40, "/images/products/tastiera-meccanica.jpg",
					ProductCategory.ACCESSORI));
			products.add(prod("Webcam HD", 49.99, 35, "/images/products/webcam-hd.jpg", ProductCategory.ACCESSORI));
			products.add(
					prod("Hub USB-C 8-in-1", 39.90, 30, "/images/products/hub-usbc.jpg", ProductCategory.ACCESSORI));
			products.add(prod("Cuffie Bluetooth", 59.99, 45, "/images/products/cuffie.jpg", ProductCategory.ACCESSORI));

			// MONITOR
			products.add(prod("Monitor 24\"", 159.00, 25, "/images/products/monitor-24.jpg", ProductCategory.MONITOR));
			products.add(prod("Monitor 27\" QHD", 249.00, 20, "/images/products/monitor-27-qhd.jpg",
					ProductCategory.MONITOR));
			products.add(prod("Monitor Ultrawide 34\"", 399.00, 12, "/images/products/monitor-ultrawide-34.jpg",
					ProductCategory.MONITOR));

			// PC_TABLETS
			products.add(prod("Notebook 15\" i5 16GB", 799.00, 18, "/images/products/notebook-15-i5.jpg",
					ProductCategory.PC_TABLETS));
			products.add(prod("Tablet 10\" WiFi 128GB", 229.00, 22, "/images/products/tablet-10.jpg",
					ProductCategory.PC_TABLETS));
			products.add(prod("PC Desktop Ryzen 5", 699.00, 14, "/images/products/desktop-ryzen5.jpg",
					ProductCategory.PC_TABLETS));

			// SMARTPHONES
			products.add(prod("Smartphone 128GB 5G", 449.00, 28, "/images/products/smartphone-128-5g.jpg",
					ProductCategory.SMARTPHONES));
			products.add(prod("Smartphone 256GB Pro", 799.00, 16, "/images/products/smartphone-256-pro.jpg",
					ProductCategory.SMARTPHONES));
			products.add(prod("Smartphone Entry 64GB", 199.00, 30, "/images/products/smartphone-64-entry.jpg",
					ProductCategory.SMARTPHONES));

			// SCANNER_STAMPANTI
			products.add(prod("Stampante WiFi A4", 129.00, 18, "/images/products/stampante-wifi-a4.jpg",
					ProductCategory.SCANNER_STAMPANTI));
			products.add(prod("Multifunzione Laser", 219.00, 14, "/images/products/multifunzione-laser.jpg",
					ProductCategory.SCANNER_STAMPANTI));
			products.add(prod("Scanner Documenti", 169.00, 10, "/images/products/scanner-documenti.jpg",
					ProductCategory.SCANNER_STAMPANTI));

			products.add(prod("Notebook Ricondizionato 14\"", 349.00, 5,
					"/images/products/notebook-ricondizionato-14.jpg", ProductCategory.USATO_RICONDIZIONATO));

			List<Product> savedProducts = productRepository.saveAll(products);

			/* ===================== PICK PRODUCTS FOR STATS TEST ===================== */
			Product pMouse = savedProducts.get(0);
			Product pTastiera = savedProducts.get(1);
			Product pWebcam = savedProducts.get(2);
			Product pMonitor24 = savedProducts.get(5);
			Product pNotebook = savedProducts.get(8);
			Product pSmart128 = savedProducts.get(11);
			Product pStampante = savedProducts.get(14);

			/*
			 * Obiettivo test: - Top selling cambia tra: - ultimi 7 giorni - ultimi 30
			 * giorni - sempre - Alcuni prodotti venduti solo "vecchi" (oltre 30) ->
			 * compaiono solo in ALL - Alcuni venduti tra 8-30 -> compaiono in 30 ma non in
			 * 7 - Alcuni venduti negli ultimi 7 -> compaiono in 7 e 30 e ALL
			 */

			LocalDateTime d2 = now.minusDays(2);
			LocalDateTime d6 = now.minusDays(6);
			LocalDateTime d10 = now.minusDays(10);
			LocalDateTime d20 = now.minusDays(20);
			LocalDateTime d40 = now.minusDays(40); // fuori dai 30

			/* ===================== ORDERS + ORDER_PRODUCTS ===================== */
			List<Order> allOrders = new ArrayList<>();

			// --- Ultimi 7 giorni: Mouse molto venduto, Webcam medio, Monitor poco
			allOrders.add(
					order(u1Saved, d2, "Ordine recente (2gg)", List.of(op(null, pMouse, 5), op(null, pWebcam, 2))));
			allOrders.add(
					order(u2Saved, d6, "Ordine recente (6gg)", List.of(op(null, pMouse, 4), op(null, pMonitor24, 1))));

			// --- 8-30 giorni: Tastiera e Notebook venduti (visibili in 30, non in 7)
			allOrders
					.add(order(u1Saved, d10, "Ordine (10gg)", List.of(op(null, pTastiera, 6), op(null, pNotebook, 1))));
			allOrders.add(
					order(u2Saved, d20, "Ordine (20gg)", List.of(op(null, pTastiera, 3), op(null, pStampante, 2))));

			// --- Oltre 30 giorni: Smartphone super venduto (visibile solo in ALL)
			allOrders.add(order(u1Saved, d40, "Ordine vecchio (40gg)", List.of(op(null, pSmart128, 12))));
			allOrders.add(order(u2Saved, d40.minusDays(3), "Ordine vecchio (43gg)",
					List.of(op(null, pSmart128, 8), op(null, pStampante, 1))));

			// collega ordini alle persone (liste mutabili)
			u1Saved.setOrders(new ArrayList<>());
			u2Saved.setOrders(new ArrayList<>());
			for (Order o : allOrders) {
				if (o.getUser().getIdUser().equals(u1Saved.getIdUser()))
					u1Saved.getOrders().add(o);
				else
					u2Saved.getOrders().add(o);
			}

			// salva a cascata (Persona -> Orders -> OrderProducts)
			userRepository.saveAll(List.of(u1Saved, u2Saved));

			System.out.println("=== SEED STATS ===");
			System.out.println("NOW = " + now);
			System.out.println("Ultimi 7 giorni dovrebbero premiare: Mouse (e un po' Webcam)");
			System.out.println("Ultimi 30 giorni: Mouse + Tastiera alti");
			System.out.println("Sempre: Smartphone 128GB 5G (vecchio) domina in ALL-time");
		};
	}

	private static void setShippingSnapshot(Order order, Persona p) {
		order.setShipFirstName(p.getFirstName());
		order.setShipLastName(p.getLastName());
		order.setShipPhone(p.getPhone());
		order.setShipAddress(p.getAddress());
		order.setShipCity(p.getCity());
	}

	private static Product prod(String name, double price, int qty, String imageUrl, ProductCategory category) {
		Product p = new Product();
		p.setName(name);
		p.setPrice(price);
		p.setQuantity(qty);
		p.setImageUrl(imageUrl);
		p.setCategory(category);
		p.setOrderProducts(new ArrayList<>());
		return p;
	}

	private static OrderProduct op(Order order, Product product, int qty) {
		return new OrderProduct(null, order, product, qty, product.getPrice());
	}

	private static Order order(Persona user, LocalDateTime when, String description, List<OrderProduct> items) {
		Order o = new Order();
		o.setIdOrder(null);
		o.setDateTime(when);
		o.setDescription(description);
		o.setUser(user);
		setShippingSnapshot(o, user);

		for (OrderProduct op : items) {
			op.setOrder(o);
		}
		o.setOrderProducts(new ArrayList<>(items));

		for (OrderProduct op : items) {
			if (op.getProduct().getOrderProducts() == null) {
				op.getProduct().setOrderProducts(new ArrayList<>());
			}
			op.getProduct().getOrderProducts().add(op);
		}

		return o;
	}
}
