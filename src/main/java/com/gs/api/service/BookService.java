package com.gs.api.service;

import com.gs.api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    Page<Book> find(Book book, Pageable pageable);

    void deleteById(Long id);

    Optional<Book> getBookByIsbn(String isbn);
}
