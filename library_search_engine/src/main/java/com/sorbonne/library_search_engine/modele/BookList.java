package com.sorbonne.library_search_engine.modele;

import lombok.Data;

import java.util.ArrayList;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/16
 */
@Data
public class BookList {
    private int count;
    private String next;
    private String previous;
    private ArrayList<Book> results;
}
