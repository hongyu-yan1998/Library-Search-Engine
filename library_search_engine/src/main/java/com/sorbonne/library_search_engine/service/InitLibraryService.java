package com.sorbonne.library_search_engine.service;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.modele.BookList;
import com.sorbonne.library_search_engine.modele.Format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/16
 */
@Service
public class InitLibraryService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * downlowd the books from the Gutendex API
     * @return
     */
    @Bean
    public Map<Integer, Book> fetchBooks() {
        File folder = new File("books");
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        Map<Integer, Book> library = new ConcurrentHashMap<>();
        ArrayList<Book> books;

        BookList bookList = restTemplate.getForObject("https://gutendex.com/books", BookList.class);
        while (library.size() < 1664) {
            books = bookList.getResults();
            for (Book book : books) {
                try {
                    // download the books
                    Format format = book.getFormats();
                    String textURL = getTextURL(format);
                    if (textURL == null) continue;
                    String text = restTemplate.getForObject(textURL, String.class);
                    if (text != null && text.split("\\s+").length > 10000) {
                        PrintWriter pw = new PrintWriter(new FileOutputStream("books/" + book.getId() + ".txt"));
                        pw.println(text);
                        pw.flush();
                        pw.close();
                        // init library
                        library.put(book.getId(), book);
                    }
                } catch (HttpClientErrorException | FileNotFoundException e) {
                }
            }
            String nextPageURL = bookList.getNext();
            bookList = restTemplate.getForObject(nextPageURL, BookList.class);
        }
        System.out.println(library.size());
        return library;
    }

    /**
     *
     * @param format
     * @return the url of the book
     */
    public String getTextURL(Format format){
        if (format.getTextUTF8() != null){
            return format.getTextUTF8();
        }
        if (format.getTextASCII() != null) {
            return format.getTextASCII();
        }
        if (format.getText() != null) {
            return format.getText();
        }
        return null;
    }
}