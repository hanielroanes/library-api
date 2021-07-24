package com.library.library.api.resource;

import com.library.library.api.dto.BookDto;
import com.library.library.api.dto.LoanDto;
import com.library.library.api.dto.LoanFilterDto;
import com.library.library.api.dto.ReturnedLoanDto;
import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import com.library.library.api.service.BookService;
import com.library.library.api.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ModelMapper modelMapper;

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

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto dto){
        Loan loan = loanService.getById(id).orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found for passed id"));
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDto> find(LoanFilterDto filter, Pageable pageRequest){
        Page<Loan> loans = loanService.find(filter, pageRequest);
        List<LoanDto> loanDtoList = loans.getContent().stream()
                .map(loan -> {
                    BookDto bookDto = modelMapper.map(loan.getBook(), BookDto.class);
                    LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
                    loanDto.setBookDto(bookDto);
                    return loanDto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(loanDtoList, pageRequest, loans.getTotalElements());
    }



}
