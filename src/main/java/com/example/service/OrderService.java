package com.example.service;

import com.example.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService {

    Page<OrderDTO> getAll(Pageable pageable);

    OrderDTO getById(Long idOrder);

    Page<OrderDTO> getByPersona(Long idUser, Pageable pageable);

    OrderDTO createForPersona(Long idUser, OrderDTO dto);

    Page<OrderDTO> searchAdmin(String customer, String product, String city,
                              LocalDateTime from, LocalDateTime to,
                              String period, Pageable pageable);

    void delete(Long idOrder);
}
