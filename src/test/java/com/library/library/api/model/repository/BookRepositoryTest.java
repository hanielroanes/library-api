package com.library.library.api.model.repository;

import com.library.library.api.model.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar true quando houver livro com Isbn igual ao informado")
    public void returnTrueWhenIsbnExists(){
        String isbn = "123";

        Book book = Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();

        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não houver livro com Isbn igual ao informado")
    public void returnFalseWhenIsbnDoesntExists(){
        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve buscar um livro pelo seu id")
    public void findByIdTest(){
        Book book = Book.builder().title("As aventuras").author("Fulano").isbn("123").build();

        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = Book.builder().title("As aventuras").author("Fulano").isbn("123").build();

        Book savedBook = repository.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = Book.builder().title("As aventuras").author("Fulano").isbn("123").build();

        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);
        Book deletedBook = entityManager.find(Book.class, book.getId());


        Assertions.assertThat(deletedBook).isNull();

    }
}
