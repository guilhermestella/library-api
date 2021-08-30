package com.gs.api.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.api.api.dto.LoanDTO;
import com.gs.api.api.dto.LoanFilterDTO;
import com.gs.api.api.dto.ReturnedLoanDTO;
import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.service.BookService;
import com.gs.api.service.LoanService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Should Create a Loan")
    public void createLoan() throws Exception {
        // given
        LoanDTO loanDTO = LoanDTO.builder().bookIsbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = createBook();
        given(bookService.getBookByIsbn(anyString())).willReturn(Optional.ofNullable(book));

        Loan loan = createLoan(book);
        given(loanService.save(any(Loan.class))).willReturn(loan);

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("bookIsbn").value("123"))
                .andExpect(jsonPath("customer").value("Fulano"));
    }

    @Test
    @DisplayName("Should not create a Loan when Isbn is invalid")
    public void invalidIsbn() throws Exception {
        // given
        LoanDTO loanDTO = LoanDTO.builder().bookIsbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);
        given(bookService.getBookByIsbn(anyString())).willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc
                .perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found"));
    }

    @Test
    @DisplayName("Should not create a Loan when Book is already loaned")
    public void cannotLoanALoanedBook() throws Exception {
        // given
        LoanDTO loanDTO = LoanDTO.builder().bookIsbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = createBook();
        given(bookService.getBookByIsbn("123")).willReturn(Optional.ofNullable(book));
        given(loanService.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        // when
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc
                .perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Should update the loan")
    public void updateLoan() throws Exception {
        // given
        long id = 1L;
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Loan loan = createLoan(createBook());
        given(loanService.getById(anyLong())).willReturn(Optional.of(loan));

        // when
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/").concat(Long.toString(id)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // when
        mvc
                .perform(req)
                .andExpect(status().isOk());
        verify(loanService, Mockito.times(1)).returnBook(loan);
    }

    @Test
    @DisplayName("Fails to update an inexisting Loan")
    public void failToUpdateLoan() throws Exception {
        // given
        long id = 1L;
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        given(loanService.getById(anyLong())).willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/").concat(Long.toString(id)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // when
        mvc
                .perform(req)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Loan not found"));
        verify(loanService, never()).returnBook(any(Loan.class));
    }

    @Test
    @DisplayName("Should list Books by Filter")
    public void shouldListBooksByFilter() throws Exception {
        //given
        Loan loan = createLoan(createBook());
        LoanFilterDTO filter = LoanFilterDTO.builder().isbn("123").customer("Fulano").build();
        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(loan), PageRequest.of(0, 100), 1));

        // when
        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                filter.getIsbn(), filter.getCustomer());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("content[0].id").value(1))
                .andExpect(jsonPath("content[0].customer").value("Fulano"))
                .andExpect(jsonPath("content[0].bookIsbn").value("123"))
                .andExpect(jsonPath("totalElements").value(1));
    }

    private Book createBook() {
        return Book.builder().id(1L).isbn("123").build();
    }

    private Loan createLoan(Book book) {
        return Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
    }
}
