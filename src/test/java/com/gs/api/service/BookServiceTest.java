package com.gs.api.service;

import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.repository.BookRepository;
import com.gs.api.repository.LoanRepository;
import com.gs.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository bookRepository;

    @MockBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setup() {
        this.service = new BookServiceImpl(bookRepository, loanRepository);
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Book book = createBook();
        when(bookRepository.save(book))
                .thenReturn(Book.builder().id(1L).isbn("123").title("As aventuras").author("Fulano").build());

        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

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
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already registered");

        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("Should get a Book by Id")
    public void getBookById() {
        // given
        Long id = 1L;
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(createBook()));

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
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

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
        when(bookRepository.existsById(anyLong())).thenReturn(true);

        // when
        service.deleteById(id);

        // then
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Fails to Find a Book By Id that Doesnt Exists")
    public void failToBindABookByIdThatDoesntExist() {
        // given
        Long id = 1L;
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        // when
        Throwable throwable = Assertions
                .catchThrowableOfType(() -> service.deleteById(id), IllegalArgumentException.class);

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
        verify(bookRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Find Book and Filter by Properties")
    public void findBooksByBookProperties() {
        // given
        Book book = createBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = Collections.singletonList(book);
        Page<Book> page = new PageImpl<>(books, pageRequest, 1);
        when(bookRepository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        // when
        Page<Book> foundBooks = service.find(book, pageRequest);

        // then
        assertThat(foundBooks.getTotalElements()).isEqualTo(1);
        assertThat(foundBooks.getContent()).isEqualTo(books);
        assertThat(foundBooks.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(foundBooks.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Fails to Find a Book by Isbn that Doesnt Exist")
    public void failToGetABookByIsbn() {
        // given
        String isbn = "123";
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

        // when
        Throwable throwable = catchThrowableOfType(() -> service.getBookByIsbn(isbn), BusinessException.class);

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book not found");
        verify(bookRepository, never()).findByIsbn(isbn);
    }

    @Test
    @DisplayName("Get loans by book")
    public void getLoansByBook() {
        // given
        Book book = createBook();
        Loan loan = Loan.builder().id(1L).book(book).customer("Fulano").build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(loanRepository.getLoansByBook(any(Book.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(loan), pageRequest, 1));

        // when
        Page<Loan> foundLoans = service.getLoansByBook(book, pageRequest);

        // then
        assertThat(foundLoans.getTotalElements()).isEqualTo(1);
        assertThat(foundLoans.getContent().get(0).getCustomer()).isEqualTo("Fulano");
        assertThat(foundLoans.getContent().get(0).getBook().getIsbn()).isEqualTo("123");
    }


    private Book createBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }
}
