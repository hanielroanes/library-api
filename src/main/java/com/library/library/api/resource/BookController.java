package com.library.library.api.resource;

import com.library.library.api.dto.BookDto;
import com.library.library.api.dto.LoanDto;
import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import com.library.library.api.service.BookService;
import com.library.library.api.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Api("Book API")
@Slf4j
public class BookController {

    @Autowired
    private BookService service;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a book")
    public BookDto create(@RequestBody @Valid BookDto dto){
        log.info("Create a book for isbn: {}", dto.getIsbn());
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDto.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Obtains a book details by id")
    public BookDto get(@PathVariable Long id){

        return service.getById(id).map(book -> modelMapper.map(book, BookDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book sucessfully deleted")
    })
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);

    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book")
    public BookDto update(@PathVariable Long id,@RequestBody BookDto dto){
        return service.getById(id).map(book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDto> find (BookDto dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter,pageRequest);
        List<BookDto> list = result.stream().map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list,pageRequest, result.getTotalElements());

    }

    @GetMapping("{id}/loans")
    @ApiOperation("Get loans from a book")
    public Page<LoanDto> loansByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found for passed id"));

        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDto> loanDtoList = result.getContent().stream().map(loan -> {
            BookDto bookDto = modelMapper.map(loan.getBook(), BookDto.class);
            LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
            loanDto.setBookDto(bookDto);
            return loanDto;
        }).collect(Collectors.toList());

        return new PageImpl<LoanDto>(loanDtoList,pageable, result.getTotalElements());
    }


}
