package com.gs.api.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@ToString
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customer;

    @ManyToOne
    @JoinColumn
    private Book book;

    private LocalDate loanDate;

    private boolean returned;

    public Loan returnBook() {
        this.returned = true;
        return this;
    }
}
