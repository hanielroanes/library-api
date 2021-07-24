package com.library.library.api.service.implementation;

import com.library.library.api.dto.LoanFilterDto;
import com.library.library.api.exceptions.BusinessException;
import com.library.library.api.model.Loan;
import com.library.library.api.model.repository.LoanRepository;
import com.library.library.api.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already Loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {

        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDto filter, Pageable pageble) {
        return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageble);
    }
}
