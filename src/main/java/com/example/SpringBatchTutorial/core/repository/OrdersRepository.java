package com.example.SpringBatchTutorial.core.repository;

import com.example.SpringBatchTutorial.core.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
}
