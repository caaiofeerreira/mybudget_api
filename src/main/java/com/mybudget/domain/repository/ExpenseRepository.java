package com.mybudget.domain.repository;

import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e WHERE e.user.id = :user")
    List<Expense> findByUserInvolved(@Param("user") Long user);


    @Query("SELECT e FROM Expense e WHERE e.user.id = :id AND e.status = :status")
    List<Expense> findByUserInvolvedAndStatus(Long id, Status status);
}