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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
        Mockito.when(repository.save(book))
                .thenReturn(Book.builder().id(1L).isbn("123").title("As aventuras").author("Fulano").build());

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

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
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book createBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }
}
