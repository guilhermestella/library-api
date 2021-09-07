package com.gs.api.api.dto;

import com.gs.api.config.ModelMapperConfig;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(ModelMapperConfig.class)
class LoanDTOTest {

    @Autowired
    ModelMapper modelMapper;

    private static final LocalDate LOAN_DATE = LocalDate.of(2020, 1, 1);

    @Test
    @DisplayName("Should map Loan to LoanDTO")
    public void mapLoanToLoanDTO() {
        // given
        Loan loan = createLoan();
        LoanDTO dto = createLoanDTO();

        // when
        LoanDTO map = modelMapper.map(loan, LoanDTO.class);

        // then
        assertThat(map.getId()).isEqualTo(dto.getId());
        assertThat(map.getBookIsbn()).isEqualTo(dto.getBookIsbn());
        assertThat(map.getCustomer()).isEqualTo(dto.getCustomer());
        assertThat(map.getCustomerEmail()).isEqualTo(dto.getCustomerEmail());
    }

    @Test
    @DisplayName("Should map LoanDTO to Loan")
    public void mapLoanDTOToLoan() {
        // given
        Loan loan = createLoan();
        LoanDTO dto = createLoanDTO();

        // when
        Loan map = modelMapper.map(dto, Loan.class);

        // then
        assertThat(map.getId()).isEqualTo(loan.getId());
        assertThat(map.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(map.getCustomerEmail()).isEqualTo(loan.getCustomerEmail());
        assertThat(map.getBook().getIsbn()).isEqualTo(loan.getBook().getIsbn());
        assertThat(map.getLoanDate()).isNull();
        assertThat(map.isReturned()).isFalse();
    }

    private LoanDTO createLoanDTO() {
        return LoanDTO.builder()
                .id(1L)
                .bookIsbn("123")
                .customer("John")
                .customerEmail("john@mail.com")
                .build();
    }

    private Loan createLoan() {
        return Loan.builder()
                .id(1L)
                .customer("John")
                .loanDate(LOAN_DATE)
                .book(createBook())
                .returned(false)
                .customerEmail("john@mail.com")
                .build();
    }

    private Book createBook() {
        return Book.builder().id(1L).author("Fulano").title("As aventuras").isbn("123").build();
    }
}