package com.library.library.api.service;

import com.library.library.api.exceptions.BusinessException;
import com.library.library.api.model.Book;
import com.library.library.api.model.repository.BookRepository;
import com.library.library.api.service.implementation.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl( repository );
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //Cenario
        Book book = createNewBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(Book.builder().id(1L)
                                                        .title(book.getTitle())
                                                        .author(book.getAuthor())
                                                        .isbn(book.getIsbn())
                                                        .build());

        //execução
        Book savedBook = service.save(book);

        //verificacao
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());

    }

    private Book createNewBook() {
        return Book.builder().title("As aventuras").author("fulano").isbn("123").build();
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar salvar livro com Isbn ja existente")
    public void shouldNotSaveABookWithDuplicatedIsbn(){

        Book book = createNewBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro pelo seu id")
    public void getByIdTest(){
        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        Assertions.assertThat( foundBook.isPresent()).isTrue();
        Assertions.assertThat( foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio quando um livro nao for encontrado pelo seu id")
    public void getNotFoundByIdTest(){
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> notFoundBook = service.getById(id);

        Assertions.assertThat( notFoundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteInvalidBookTest(){
        Book book = createNewBook();

        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));

        Assertions.assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");

        Mockito.verify(repository, Mockito.never()).delete(book);

    }

    @Test
    @DisplayName("Deve atualizar um livro pelo seu id")
    public void updateBookTest(){
        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);

        Book updatedBook = Book.builder().id(id).title("Estrada longa").author("ciclano").isbn("123").build();

        Mockito.when(repository.save(book)).thenReturn(updatedBook);

        updatedBook = service.update(book);

        Assertions.assertThat( updatedBook.getId()).isEqualTo(id);
        Assertions.assertThat( updatedBook.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat( updatedBook.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat( updatedBook.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar um livro com id inexistente")
    public void shoudNotUpdateBookWithNullId(){

        Book book = createNewBook();

        Throwable exception = Assertions.catchThrowable(() -> service.update(book));

        Assertions.assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve filtrar uma lista de livros pelas propriedades")
    public void findBooksTest(){
         Book book = createNewBook();

        PageRequest pageRequest = PageRequest.of(0,10);

        List<Book> list = Arrays.asList(book);
        Page<Book> bookPage = new PageImpl<>(list, pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                        .thenReturn(bookPage);

        Page<Book> books = service.find(book, pageRequest);

        Assertions.assertThat(books.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(books.getContent()).isEqualTo(list);
        Assertions.assertThat(books.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(books.getPageable().getPageSize()).isEqualTo(10);

    }


}