package com.library.repository;

import com.library.entity.Transaction;
import com.library.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMemberIdOrderByIssueDateDesc(Long memberId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findAllByOrderByIssueDateDesc();
}
