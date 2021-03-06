package com.library.library.api.model.repository;

import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {


    @Query("select case when ( count(l.id) > 0 ) then true else false end from Loan l " +
            "where l.book = :book and (l.returned is null or l.returned is false) ")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    Page<Loan> findByBookIsbnOrCustomer(String isbn, String customer, Pageable pageRequest);

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query("select l from Loan l where l.loanDate <= :days and (l.returned is null or l.returned is false) ")
    List<Loan> findbyLoanDateLessThanAndNotReturned(@Param("days") LocalDate treeDaysAgo);
}
