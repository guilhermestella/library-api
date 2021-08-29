package com.gs.api.service;

import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.repository.BookRepository;
import com.gs.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Book book = createBook();
        when(repository.save(book))
                .thenReturn(Book.builder().id(1L).isbn("123").title("As aventuras").author("Fulano").build());

        when(repository.existsByIsbn(anyString())).thenReturn(false);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }


    @Test
    @DisplayName("Cannot save Book with an Existing ISBN")
    public void shouldNotSaveBookWithExistingIsbn() {
        Book book = createBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Should get a Book by Id")
    public void getBookById() {
        // given
        Long id = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(createBook()));

        // when
        Optional<Book> foundBook = service.getById(id);

        // then
        Assertions
                .assertThat(foundBook)
                .isNotEmpty();
    }

    @Test
    @DisplayName("Should return Empty if Book not Found by Id")
    public void bookNotFoundById() {
        // given
        Long id = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Optional<Book> foundBook = service.getById(id);

        // then
        Assertions
                .assertThat(foundBook)
                .isEmpty();
    }

    @Test
    @DisplayName("Should Delete a Book by ID")
    public void shouldDeleteBook() {
        // given
        Long id = 1L;
        when(repository.existsById(anyLong())).thenReturn(true);

        // when
        service.deleteById(id);

        // then
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should Throw Illegal Argument Exception When Book Dont Exist")
    public void shouldThrowIllegalArgumentExceptionWhenBookDontExist() {
        // given
        Long id = 1L;
        when(repository.existsById(anyLong())).thenReturn(false);

        // when
        Throwable throwable = Assertions
                .catchThrowableOfType(() -> service.deleteById(id), IllegalArgumentException.class);

        // then
        verify(repository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Find Book and Filter by Properties")
    public void shouldFindBooksByProperties() {
        // given
        Book book = createBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = Collections.singletonList(book);
        Page<Book> page = new PageImpl<>(books, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // when
        Page<Book> foundBooks = service.find(book, pageRequest);

        // then
        assertThat(foundBooks.getTotalElements()).isEqualTo(1);
        assertThat(foundBooks.getContent()).isEqualTo(books);
        assertThat(foundBooks.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(foundBooks.getPageable().getPageSize()).isEqualTo(10);
    }


    private Book createBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }
}
