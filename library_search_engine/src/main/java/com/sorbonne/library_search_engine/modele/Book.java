package com.sorbonne.library_search_engine.modele;

import lombok.Data;

import java.util.ArrayList;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */
@Data
public class Book {
    private int id;
    private String title;
    private ArrayList<String> subjects;
    private ArrayList<Person> authors;
    private ArrayList<Person> translators;
    private ArrayList<String> bookshelves;
    private ArrayList<String> languages;
    private boolean copyright;
    private String media_type;
    private Format formats;
    private int download_count;
}
