package com.gs.api.service.impl;

import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.repository.BookRepository;
import com.gs.api.service.BookService;
import org.springframework.stereotype.Service;

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
}
