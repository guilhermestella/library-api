package com.gs.api.api.dto;

import com.gs.api.config.ModelMapperConfig;
import com.gs.api.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(ModelMapperConfig.class)
class BookDTOTest {

    @Autowired
    ModelMapper modelMapper;

    @Test
    @DisplayName("Should map Book to BookDTO")
    public void mapBookToBookDTO() {
        // given
        Book book = createBook();
        BookDTO dto = createBookDTO();

        // when
        BookDTO map = modelMapper.map(book, BookDTO.class);

        // then
        assertThat(map.getId()).isEqualTo(dto.getId());
        assertThat(map.getAuthor()).isEqualTo(dto.getAuthor());
        assertThat(map.getTitle()).isEqualTo(dto.getTitle());
        assertThat(map.getIsbn()).isEqualTo(dto.getIsbn());
    }

    @Test
    @DisplayName("Should map BookDTO to Book")
    public void mapBookDTOToBook() {
        // given
        Book book = createBook();
        BookDTO dto = createBookDTO();

        // when
        Book map = modelMapper.map(dto, Book.class);

        // then
        assertThat(map.getId()).isEqualTo(book.getId());
        assertThat(map.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(map.getTitle()).isEqualTo(book.getTitle());
        assertThat(map.getIsbn()).isEqualTo(book.getIsbn());
    }

    private BookDTO createBookDTO() {
        return BookDTO.builder().id(1L).author("Fulano").title("As aventuras").isbn("123").build();
    }

    private Book createBook() {
        return Book.builder().id(1L).author("Fulano").title("As aventuras").isbn("123").build();
    }

}