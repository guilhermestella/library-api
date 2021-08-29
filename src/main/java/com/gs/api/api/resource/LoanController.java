package com.gs.api.api.resource;

import com.gs.api.api.dto.LoanDTO;
import com.gs.api.api.exceptions.ApiErrors;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.service.BookService;
import com.gs.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO post(@RequestBody LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found"));
        Loan loan = Loan
                .builder()
                .loanDate(LocalDate.now())
                .customer(dto.getCustomer())
                .book(book)
                .build();
        loan = loanService.save(loan);
        return modelMapper.map(loan, LoanDTO.class);
    }
}
