package com.library.library.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.library.api.dto.BookDto;
import com.library.library.api.exceptions.BusinessException;
import com.library.library.api.model.Book;
import com.library.library.api.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {

        BookDto dto = createNewBook();

        Book savedBook = Book.builder().id(1L).author("Autor")
                .title("Meu livro")
                .isbn("123").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isCreated())
                .andExpect(jsonPath("id").value(savedBook.getId()))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não forem informados os dados obrigatorios.")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDto());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));


    }

    @Test
    @DisplayName("Deve lançar erro caso haja a tentativa de salvar livro com Isbn duplicado")
    public void creatBookWithDuplicatedIbn() throws Exception {
        BookDto dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);

        String messageError = "Isbn já cadastrado.";

        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(messageError));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));

    }

    @Test
    @DisplayName("Deve obter os detalhes de um livro")
    public void getBookDetailsTest() throws Exception {

        Long id = 1L;

        Book book = Book.builder()
                    .id(1L)
                    .title(createNewBook().getTitle())
                    .author(createNewBook().getAuthor())
                    .isbn(createNewBook().getIsbn())
                    .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
                                                        .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect( status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar notfound caso não exista o livro buscado")
    public void bookNotFoundTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletarum livro")
    public void deleteBookTeste() throws Exception {
        Long id = 1L;

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder()
                                                                                    .id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar notFound caso não haja o livro há ser excluido")
    public void deleteInexistingBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {
        Long id = 1L;

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book bookUpdate = Book.builder()
                .id(1L)
                .title("Os contos")
                .author("Machado de Assis")
                .isbn("321")
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(bookUpdate));

        BDDMockito.given(service.update(bookUpdate)).willReturn(Book.builder().id(id)
                                                                            .author("Autor")
                                                                            .title("Meu livro")
                                                                            .isbn("123").build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar notFound ao tentar atualizar um livro inexistente")
    public void updateInexistingBookTest() throws Exception {
        Long id = 1L;

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {
        Long id = 1L;

        Book book = Book.builder()
                    .id(id)
                    .author(createNewBook().getAuthor())
                    .title(createNewBook().getTitle())
                    .isbn(createNewBook().getIsbn())
                    .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                        .willReturn(new PageImpl<>(Arrays.asList(book), PageRequest.of(0,100), 1));

        String query = String.format("?title=%s&author=%s&page=0&size=100",
                                    book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(BOOK_API.concat(query))
                                                        .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDto createNewBook() {
        return BookDto.builder().author("Autor")
                .title("Meu livro")
                .isbn("123").build();
    }

}

