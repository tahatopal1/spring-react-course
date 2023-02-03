package com.project.demo.responsemodels;

import com.project.demo.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShelfCurrentLoansReponse {

    private Book book;
    private int daysLeft;

}
