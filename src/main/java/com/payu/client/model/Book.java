package com.payu.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Book {

    private long id;

    private String name;

    private String isbnNumber;

    private String publishDate;

    private double price;

    private BookType bookType;

}
