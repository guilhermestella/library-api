package com.gs.api.api.resource;

import com.gs.api.api.dto.LoanDTO;
import com.gs.api.api.dto.LoanFilterDTO;
import com.gs.api.api.dto.ReturnedLoanDTO;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.service.BookService;
import com.gs.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO post(@RequestBody @Valid LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getBookIsbn())
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

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void patch(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        loanService
                .getById(id)
                .map(l -> dto.isReturned() ? l.returnBook() : l.undoReturn())
                .map(loanService::update)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> get(LoanFilterDTO filter, Pageable pageable) {
        Page<Loan> loans = loanService
                .find(filter, pageable);
        List<LoanDTO> dtos = loans
                .stream()
                .map(l -> modelMapper.map(l, LoanDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, loans.getTotalElements());
    }
}
