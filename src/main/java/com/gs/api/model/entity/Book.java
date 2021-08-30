package com.gs.api.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Book {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Setter
    private String title;

    @Column
    @Setter
    private String author;

    @Column
    private String isbn;

    @OneToMany(mappedBy = "book")
    private List<Loan> loans;
}
