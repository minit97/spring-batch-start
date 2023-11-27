package com.example.SpringBatchTutorial.core.repository;

import com.example.SpringBatchTutorial.core.domain.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
