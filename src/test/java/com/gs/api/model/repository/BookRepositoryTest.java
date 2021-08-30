package com.gs.api.model.repository;

import com.gs.api.model.entity.Book;
import com.gs.api.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Should be true when exists a book by Isbn")
    public void returnTrueWhenExistsBookByIsbn() {
        //given
        String isbn = "123";
        Object book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book);

        //when
        boolean exists = repository.existsByIsbn(isbn);

        //then returns
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find a Book By Id")
    public void findById() {
        // given
        Book newBook = createNewBook();
        entityManager.persist(newBook);

        // when
        Optional<Book> foundBook = repository.findById(newBook.getId());

        // then
        assertThat(foundBook).isPresent();
    }

    @Test
    @DisplayName("Should save a book")
    public void shouldSaveBook() {
        // given
        Book newBook = createNewBook();

        // when
        Book savedBook = repository.save(newBook);

        // then
        Book foundBook = entityManager.find(Book.class, savedBook.getId());
        assertThat(foundBook).isNotNull();
    }

    @Test
    @DisplayName("Should delete a book")
    public void shouldDeleteABook() {
        // given
        Book newBook = createNewBook();
        Book savedBook = entityManager.persist(newBook);

        // when
        repository.delete(savedBook);

        // then
        Book deletedBook = entityManager.find(Book.class, savedBook.getId());
        assertThat(deletedBook).isNull();
    }

    @Test
    @DisplayName("Should find a book by isbn")
    public void shouldFindABookByIsbn() {
        // given
        Book book = createNewBook();
        Book savedBook = entityManager.persist(book);

        // when
        Optional<Book> foundBook = repository.findByIsbn(book.getIsbn());

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(savedBook.getId());
    }

    @Test
    @DisplayName("Fails to find a book by isbn")
    public void failToFindBookByIsbn() {
        // given
        String isbn = "123";

        // when
        Optional<Book> foundBook = repository.findByIsbn(isbn);

        // then
        assertThat(foundBook).isEmpty();
    }

    public Book createNewBook() {
        return Book.builder().title("Aventuras").author("Fulano").isbn("123").build();
    }
}
