package com.library.library.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    private Long id;
    private BookDto bookDto;
    private String isbn;
    private String customer;
}
