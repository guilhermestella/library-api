package com.gs.api.service.impl;

import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.repository.BookRepository;
import com.gs.api.repository.LoanRepository;
import com.gs.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Override
    public Book save(Book book) {
        if ( bookRepository.existsByIsbn(book.getIsbn()) ) {
            throw new BusinessException("Isbn already registered");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Page<Book> find(Book book, Pageable pageable) {
        Example<Book> example = Example.of(
                book,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.STARTING)
        );
        return bookRepository.findAll(example, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if ( !bookRepository.existsById(id) ) {
            throw new IllegalArgumentException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        if ( !bookRepository.existsByIsbn(isbn) ) {
            throw new BusinessException("Book not found");
        }
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return loanRepository.findLoansByBook(book, pageable);
    }
}
