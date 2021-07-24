package com.library.library.api.model.repository;

import com.library.library.api.model.Book;
import com.library.library.api.model.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se o livro não esta emprestado")
    public void existsByBookAndNotReturnedTest(){

        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve obter emprestimos que estao atrasados")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).loanDate(LocalDate.now().minusDays(5)).customer("Fulano").build();
        entityManager.persist(loan);

        List<Loan> loans = repository.findbyLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(loans).contains(loan);
    }

    @Test
    @DisplayName("Deve retorar vazio ja que o emprestimo nao esta atrasado")
    public void NotFindByLoanDateLessThanAndNotReturnedTest(){
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).loanDate(LocalDate.now()).customer("Fulano").build();
        entityManager.persist(loan);

        List<Loan> loans = repository.findbyLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(loans).isEmpty();
    }

    private Book createNewBook(){
        return Book.builder().title("As aventuras").author("Fulano").isbn("123").build();
    }
}
