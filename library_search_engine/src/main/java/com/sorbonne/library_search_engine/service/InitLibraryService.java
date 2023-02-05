package com.sorbonne.library_search_engine.service;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.modele.BookList;
import com.sorbonne.library_search_engine.modele.Format;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/16
 */
@Slf4j
@Service
public class InitLibraryService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * downlowd the books from the Gutendex API
     * @return
     */
    @Bean
    public Map<Integer, Book> library() throws IOException, ClassNotFoundException {
        Map<Integer, Book> library = new HashMap<>();
        ArrayList<Book> books;

        // Books have been initialized
        if(new File("books.ser").exists()) {
            log.info("Books have been downloaded, start initializing the library");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books.ser"));
            library = (Map<Integer, Book>) ois.readObject();
            ois.close();
            return library;
        }

        File folder = new File("books");
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        log.info("Start downloading books from Gutendex API and initializing the library");
        BookList bookList = restTemplate.getForObject("https://gutendex.com/books", BookList.class);
        while (library.size() < 1500) {
            books = bookList.getResults();
            for (Book book : books) {
                try {
                    // download the books
                    Format format = book.getFormats();
                    String textURL = getTextURL(format);
                    if (textURL == null) continue;
                    book.setContent(textURL);
                    if (format.getCoverImage() != null) book.setImage(format.getCoverImage());
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
            log.info(library.size() + " books have been downloaded");
            String nextPageURL = bookList.getNext();
            bookList = restTemplate.getForObject(nextPageURL, BookList.class);
        }

        log.info("saving books.ser file");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.ser"));
        oos.writeObject(library);
        oos.flush();
        oos.close();

        log.info("Congratulations ! " + library.size() + " books have been downloaded");
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