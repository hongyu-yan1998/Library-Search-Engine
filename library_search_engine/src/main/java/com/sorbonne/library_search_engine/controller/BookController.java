package com.sorbonne.library_search_engine.controller;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping("/books")
@Slf4j
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        ArrayList<Book> books = new ArrayList<>(bookService.getLibrary().values());
        return ResponseEntity.ok(books); // 200 OK
    }

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

    @GetMapping("/suggestion/{id}")
    public ResponseEntity<List<Book>> getRecommendedBooks(@PathVariable int id) {
        log.info("Get recommended books for the book : " + id);

        ArrayList<Book> books = (ArrayList<Book>) bookService.getNeighborBookByJaccard(id);
        return ResponseEntity.ok(books); // 200 OK
    }
}
