package com.gs.api.model.repository;

import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.repository.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Verify if exists a Loan with a not returned book")
    public void existsByBookAndNotReturned() {
        // given
        Book book = createBook();
        entityManager.persist(book);
        Loan loan = createLoan(book);
        entityManager.persist(loan);

        // when
        boolean isNotReturned = repository.existsByBookIdAndReturnedIsFalse(book);

        // then
        assertThat(isNotReturned).isTrue();
    }

    @Test
    @DisplayName("Find Loans by Isbn")
    public void findLoansByIsbn() {
        // given
        String isbnFilter = "123";
        String customerFilter = "";
        Book book = createBook();
        entityManager.persist(book);
        Loan loan = createLoan(book);
        entityManager.persist(loan);

        // when
        Page<Loan> foundLoans = repository
                .findByBookIsbnOrCustumer(isbnFilter, customerFilter, PageRequest.of(0, 10));

        // then
        assertThat(foundLoans.getTotalElements()).isEqualTo(1);
        assertThat(foundLoans.getContent().get(0).getBook().getIsbn()).isEqualTo("123");
        assertThat(foundLoans.getContent().get(0).getCustomer()).isEqualTo("Fulano");
    }

    @Test
    @DisplayName("Find Loans by Customer")
    public void findLoansByCustomer() {
        // given
        String isbnFilter = "";
        String customerFilter = "Fulano";
        Book book = createBook();
        entityManager.persist(book);
        Loan loan = createLoan(book);
        entityManager.persist(loan);

        // when
        Page<Loan> foundLoans = repository
                .findByBookIsbnOrCustumer(isbnFilter, customerFilter, PageRequest.of(0, 10));

        // then
        assertThat(foundLoans.getTotalElements()).isEqualTo(1);
        assertThat(foundLoans.getContent().get(0).getBook().getIsbn()).isEqualTo("123");
        assertThat(foundLoans.getContent().get(0).getCustomer()).isEqualTo("Fulano");
    }

    @Test
    @DisplayName("Find Loans by Customer")
    public void cannotFindAnyLoansByInvalidFilters() {
        // given
        String isbnFilter = "321";
        String customerFilter = "Ciclano";
        Book book = createBook();
        entityManager.persist(book);
        Loan loan = createLoan(book);
        entityManager.persist(loan);

        // when
        Page<Loan> foundLoans = repository
                .findByBookIsbnOrCustumer(isbnFilter, customerFilter, PageRequest.of(0, 10));

        // then
        assertThat(foundLoans.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Find Loans by Book")
    public void findLoansByBook() {
        // given
        Book book1 = createBook();
        entityManager.persist(book1);

        Book book2 = createAnotherBook();
        entityManager.persist(book2);

        Loan loan1 = createLoan(book1);
        entityManager.persist(loan1);

        Loan loan2 = createLoan(book1);
        entityManager.persist(loan2);

        Loan loan3 = createLoan(book2);
        entityManager.persist(loan3);

        // when
        Page<Loan> foundLoans = repository.findLoansByBook(book1, PageRequest.of(0, 10));

        // then
        assertThat(foundLoans.getTotalElements()).isEqualTo(2);
        assertThat(foundLoans.getContent().get(0).getBook()).isEqualTo(book1);
        assertThat(foundLoans.getContent().get(1).getBook()).isEqualTo(book1);
        Throwable throwable = Assertions.catchThrowable(() -> foundLoans.getContent().get(2));
        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Find not returned loans after date")
    void findNotReturnedLoansAfterDate() {
        // given
        LocalDate verificationDate = LocalDate.of(2000, 1, 6);
        int daysConsideredAsLate = 3;
        Book book1 = createBook();
        entityManager.persist(book1);
        Loan loan1 = createLoanToReturnAt(book1, LocalDate.of(2000, 1, 2));
        entityManager.persist(loan1);
        Loan loan2 = createLoanToReturnAt(book1, LocalDate.of(2000, 1, 5));
        entityManager.persist(loan2);
        Loan loan3 = createLoanToReturnAt(book1, LocalDate.of(2000, 1, 2));
        loan3.returnBook();
        entityManager.persist(loan3);

        // when
        List<Loan> foundLoans = repository.findNotReturnedLoansAfterDay(verificationDate, daysConsideredAsLate);

        // then
        assertThat(foundLoans.size()).isEqualTo(1);
        assertThat(foundLoans.get(0)).isEqualTo(loan2);
    }

    private Loan createLoanToReturnAt(Book book, LocalDate localDate) {
        return Loan.builder().loanDate(localDate).returned(false).book(book).customer("Fulano").build();
    }

    private Loan createLoan(Book book) {
        return Loan.builder().loanDate(LocalDate.now()).returned(false).book(book).customer("Fulano").build();
    }

    private Book createBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    private Book createAnotherBook() {
        return Book.builder().isbn("456").author("Ciclano").title("As desventuras").build();
    }
}