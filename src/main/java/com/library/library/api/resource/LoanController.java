package com.library.library.api.resource;

import com.library.library.api.dto.LoanDto;
import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import com.library.library.api.service.BookService;
import com.library.library.api.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDto dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder().book(book)
                                    .customer(dto.getCustomer())
                                    .loanDate(LocalDate.now())
                                    .build();

        entity = loanService.save(entity);

        return entity.getId();
    }


}
