package com.library.library.api.service;

import com.library.library.api.dto.LoanFilterDto;
import com.library.library.api.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDto filter, Pageable pageble);
}
