package com.gs.api.service.impl;

import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.repository.BookRepository;
import com.gs.api.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if ( repository.existsByIsbn(book.getIsbn()) ) {
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
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
        return repository.findAll(example, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if ( !repository.existsById(id) ) {
            throw new IllegalArgumentException("Book not found");
        }
        repository.deleteById(id);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return Optional.empty();
    }
}
