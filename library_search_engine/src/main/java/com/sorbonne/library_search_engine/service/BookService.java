package com.sorbonne.library_search_engine.service;

import com.sorbonne.library_search_engine.modele.Book;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/2
 */
@Service
public class BookService {
    private Map<Integer, Book> books;

    public Book getBookById(int id){
        try {
            return books.get(id);
        }catch (NullPointerException e){
            return null;
        }
    }


}
