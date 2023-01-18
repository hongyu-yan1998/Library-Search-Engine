package com.sorbonne.library_search_engine.modele;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */
@Data
public class Book implements Serializable {
    private int id;
    private String title;
    private ArrayList<String> subjects;
    private ArrayList<Person> authors;
    private ArrayList<String> bookshelves;
    private ArrayList<String> languages;
    private Format formats;
    private int download_count;
    // URL of text
    private String content;
    // URL of image
    private String image;
}
