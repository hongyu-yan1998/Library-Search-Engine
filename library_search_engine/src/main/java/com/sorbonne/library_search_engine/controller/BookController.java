package com.sorbonne.library_search_engine.controller;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */

@RestController
@RequestMapping("/books")
@Slf4j
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookService.getBookById(id);
        if (book != null)
            return ResponseEntity.ok(book); // 200 OK
        else
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
    }

    @GetMapping("/search/{content}")
    public ResponseEntity<List<Book>> getBooksByKeyword(@PathVariable String content) {
        log.info("Search : " + content);

        // search keyword
        String[] keywords = content.split("\\s+");
        HashSet<Book> booksSet = new HashSet<>();
        for (String keyword: keywords) {
            booksSet.addAll(bookService.getBooksByKeyword(keyword));
        }

        // search regex
        booksSet.addAll(bookService.getBooksByRegex(content));

        ArrayList<Book> books = new ArrayList<>(booksSet);
        bookService.sortBooksByCloseness(books);
        return ResponseEntity.ok(books);
    }

}
