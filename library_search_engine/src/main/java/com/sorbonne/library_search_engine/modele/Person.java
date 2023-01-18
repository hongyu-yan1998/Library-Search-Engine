package com.sorbonne.library_search_engine.modele;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author Hongyu YAN
 * @Date 2023/1
 */
@Data
public class Person implements Serializable {
    private String name;
    private int birth_year;
    private int death_year;
}
