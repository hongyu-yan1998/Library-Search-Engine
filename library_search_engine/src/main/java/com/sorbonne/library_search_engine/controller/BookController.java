package com.sorbonne.library_search_engine.controller;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("{id}")
    public Book getBookById(@PathVariable Integer id) {
        System.out.println("springboot is running..." + id);
        Book book = bookService.getBookById(id);
        return book;
    }

}
