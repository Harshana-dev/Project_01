package com.harshana.gemstore.repository;

import com.harshana.gemstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> { }