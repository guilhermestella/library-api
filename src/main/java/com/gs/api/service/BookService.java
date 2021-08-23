package com.gs.api.service;

import com.gs.api.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void deleteById(Long id);
}
