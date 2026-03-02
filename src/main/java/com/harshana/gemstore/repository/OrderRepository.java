package com.harshana.gemstore.repository;

import com.harshana.gemstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByPhoneOrderByCreatedAtDesc(String phone);

    Optional<Order> findByIdAndPhone(Long id, String phone);
}