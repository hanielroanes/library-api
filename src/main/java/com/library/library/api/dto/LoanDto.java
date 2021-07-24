package com.library.library.api.dto;

import com.library.library.api.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    private Long id;
    private BookDto bookDto;
    private String isbn;
    private String customer;
    private String customerEmail;
}
