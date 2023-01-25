package com.sorbonne.library_search_engine.utils.keywords;

import lombok.Data;

import java.util.HashMap;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/25
 */
@Data
public class KeywordDictionary {
    // the frequency of keywords appearing in each document
    private HashMap<String, HashMap<Integer, Double>> keywordTable;
    // the keywords contained in the article and its frequency
    private HashMap<Integer, HashMap<String, Double>> keywordBookTable;
}
