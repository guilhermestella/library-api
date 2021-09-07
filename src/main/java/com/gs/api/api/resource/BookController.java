package com.gs.api.api.resource;

import com.gs.api.api.dto.BookDTO;
import com.gs.api.api.dto.LoanDTO;
import com.gs.api.model.entity.Book;
import com.gs.api.model.entity.Loan;
import com.gs.api.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Api("Book API")
@Slf4j
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a Book")
    public BookDTO post(@RequestBody @Valid BookDTO dto) {
        log.info(" creating a book for isbn: {} ", dto.getIsbn());
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Get a Book by Id")
    public BookDTO get(@PathVariable Long id) {
        return service.getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a Book by Id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted!")
    })
    public void delete(@PathVariable Long id) {
        service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.deleteById(id);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update a Book by Id")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
        return service
                .getById(id)
                .map(book -> {
                    book.setAuthor(dto.getAuthor());
                    book.setTitle(dto.getTitle());
                    book = service.save(book);
                    return modelMapper.map(book, BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("List all Books")
    public Page<BookDTO> list(BookDTO filter, Pageable pageable) {
        Book book = modelMapper.map(filter, Book.class);
        Page<Book> books = service.find(book, pageable);
        List<BookDTO> dtos = books
                .stream()
                .map(b -> modelMapper.map(b, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, books.getTotalElements());
    }

    @GetMapping(value = "{id}/loans")
    @ApiOperation("List all Book's Loans")
    public Page<LoanDTO> listLoans(@PathVariable Long id, Pageable pageable) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Page<Loan> pagedLoans = service.getLoansByBook(book, pageable);
        return pagedLoans.map(l -> modelMapper.map(l, LoanDTO.class));
    }
}
