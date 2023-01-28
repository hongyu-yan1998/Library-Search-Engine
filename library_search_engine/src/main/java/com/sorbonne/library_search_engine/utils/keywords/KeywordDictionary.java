package com.sorbonne.library_search_engine.utils.keywords;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/25
 */
@Data
public class KeywordDictionary implements Serializable {
    private HashMap<String, String> word2stem;
    // the frequency of keywords appearing in each document
    private HashMap<String, HashMap<Integer, Double>> keywordTable;
    // the keywords contained in the article and its frequency
    private HashMap<Integer, HashMap<String, Double>> keywordBookTable;

    public KeywordDictionary(HashMap<String, String> word2stem,
                             HashMap<String, HashMap<Integer, Double>> keywordTable,
                             HashMap<Integer, HashMap<String, Double>> keywordBookTable) {
        this.word2stem = word2stem;
        this.keywordTable = keywordTable;
        this.keywordBookTable = keywordBookTable;
    }
}
