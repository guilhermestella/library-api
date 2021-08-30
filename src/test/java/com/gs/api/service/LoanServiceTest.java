package com.gs.api.service;

import com.gs.api.api.dto.LoanFilterDTO;
import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.repository.LoanRepository;
import com.gs.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceTest {

    static LocalDate LOAN_DATE = LocalDate.of(2020, 1, 1);

    LoanService service;

    @Mock
    LoanRepository repository;

    @BeforeEach
    public void setup() {
        service = new LoanServiceImpl(repository);
    }

    @Test
    void createLoan() {
        // given
        Book book = createBook();
        Loan loan = createLoan(book);
        when(repository.existsByBookIdAndReturnedIsFalse(any(Book.class))).thenReturn(false);
        when(repository.save(any(Loan.class))).thenReturn(createLoanWithId(book, 1L));

        // when
        Loan savedLoan = service.save(loan);

        // then
        assertThat(savedLoan.getId()).isNotNull();
        assertThat(savedLoan.isReturned()).isFalse();
    }

    @Test
    @DisplayName("Fails to loan a book that is already loaned")
    void failToCreateLoanWithAlreadyLoanedBook() {
        // given
        Book book = createBook();
        Loan loan = createLoan(book);
        when(repository.existsByBookIdAndReturnedIsFalse(any(Book.class))).thenReturn(true);

        // when
        BusinessException throwable = catchThrowableOfType(() -> service.save(loan), BusinessException.class);

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");
        verify(repository, never()).save(loan);
    }

    @Test
    @DisplayName("Should find a Loan by Id")
    void getLoanInfoById() {
        // given
        Long id = 1L;
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(createLoanWithId(createBook(), id)));

        // when
        Optional<Loan> foundLoan = service.getById(id);

        // then
        assertThat(foundLoan).isPresent();
    }

    @Test
    @DisplayName("Fail to find a Loan by Id")
    void failsToFindById() {
        // given
        Long id = 1L;
        when(repository.existsById(anyLong())).thenReturn(false);

        // when
        BusinessException throwable = catchThrowableOfType(() -> service.getById(id), BusinessException.class);

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Loan not found");
        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Update a loan")
    void updateLoan() {
        // given
        long id = 1L;
        Loan loan = createLoanWithId(createBook(), id);

        // when
        service.returnBook(loan);

        // then
        assertThat(loan.isReturned()).isTrue();
        verify(repository, times(1)).save(loan);
    }

    @Test
    @DisplayName("Find Loans by Filter")
    void findLoansByFilter() {
        // given
        LoanFilterDTO filter = LoanFilterDTO.builder().isbn("123").customer("Fulano").build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Loan> loans = Collections.singletonList(createLoanWithId(createBook(), 1L));
        when(repository.findByBookIsbnOrCustumer(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(loans, pageable, loans.size()));

        // when
        Page<Loan> returnedLoans = service.find(filter, pageable);

        // then
        assertThat(returnedLoans.getTotalElements()).isEqualTo(1);
        assertThat(returnedLoans.getContent()).isEqualTo(loans);
    }


    private Loan createLoanWithId(Book book, Long id) {
        return Loan.builder().id(id).loanDate(LOAN_DATE).book(book).customer("Fulano").returned(false).build();
    }

    private Loan createLoan(Book book) {
        return Loan.builder().book(book).customer("Fulano").loanDate(LOAN_DATE).returned(false).build();
    }

    private Book createBook() {
        return Book.builder().id(1L).isbn("123").author("Fulano").title("As aventuras").build();
    }
}