package com.library.library.api.service;

import com.library.library.api.dto.LoanFilterDto;
import com.library.library.api.exceptions.BusinessException;
import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import com.library.library.api.model.repository.LoanRepository;
import com.library.library.api.service.implementation.LoanServiceImpl;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest(){
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().id(1L)
                                        .loanDate(LocalDate.now())
                                        .customer(customer)
                                        .book(book)
                                        .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
    }

    @Test
    @DisplayName("Verifica se o livro a ser salvo ja não esta emprestado")
    public void loanedBookSaveTest(){

        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> service.save(savingLoan));

        assertThat(throwable).isInstanceOf(BusinessException.class).hasMessage("Book already Loaned");
        verify(repository, never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve retornar um livro pelo seu id")
    public void findByIdTest(){
        Loan loan = createLoan();
        long id = 1L;
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());

        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest(){
        Loan loan = createLoan();
        long id = 1L;
        loan.setId(id);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);
        Loan updateLoan = service.update(loan);

        assertThat(updateLoan.getReturned()).isTrue();
        verify(repository, times(1)).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar uma lista de livros pelas propriedades")
    public void findBooksTest(){
        Loan loan = createLoan();
        long id = 1L;
        loan.setId(id);

        LoanFilterDto filter = LoanFilterDto.builder().customer("Fulano").isbn("123").build();

        PageRequest pageRequest = PageRequest.of(0,10);

        List<Loan> list = Arrays.asList(loan);
        Page<Loan> loanPage = new PageImpl<>(list, pageRequest, 1);

        when(repository.findByBookIsbnOrCustomer(Mockito.anyString(),Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(loanPage);

        Page<Loan> loans = service.find(filter, pageRequest);

        assertThat(loans.getTotalElements()).isEqualTo(1);
        assertThat(loans.getContent()).isEqualTo(list);
        assertThat(loans.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(loans.getPageable().getPageSize()).isEqualTo(10);

    }

    public static Loan createLoan(){
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
}
