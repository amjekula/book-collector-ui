package com.payu.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Book {

    private long id;

    private String name;

    private String isbnNumber;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private String publishDate;

    private double price;

    private BookType bookType;

}
