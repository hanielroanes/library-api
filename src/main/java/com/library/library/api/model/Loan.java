package com.library.library.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customer;

    @Column(name = "costomer_email")
    private String customerEmail;

    @ManyToOne
    @JoinColumn(name = "id_book")
    private Book book;
    private LocalDate loanDate;
    private Boolean returned;
}
