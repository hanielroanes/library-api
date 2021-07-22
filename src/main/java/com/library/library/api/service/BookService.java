package com.library.library.api.service;


import com.library.library.api.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book book, Pageable pageable);

    Optional<Book> getBookByIsbn(String isbn);
}
