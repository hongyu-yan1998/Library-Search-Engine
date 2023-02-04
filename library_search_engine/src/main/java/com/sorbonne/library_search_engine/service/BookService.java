package com.sorbonne.library_search_engine.service;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.utils.keywords.KeywordDictionary;
import com.sorbonne.library_search_engine.utils.research.DFAutomaton;
import com.sorbonne.library_search_engine.utils.research.NDFAutomaton;
import com.sorbonne.library_search_engine.utils.research.RegEx;
import com.sorbonne.library_search_engine.utils.research.RegExTree;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/2
 */
@Service
@Slf4j
@Data
public class BookService {
    @Autowired
    private Map<Integer, Book> library;
    @Autowired
    private KeywordDictionary keywordDictionary;
    @Autowired
    LinkedHashMap<Integer, Double> closenessCentrality;
    @Autowired
    HashMap<Integer, ArrayList<Integer>> jaccardMatriceNeighbor;


    public Book getBookById(int id){
        try {
            return library.get(id);
        }catch (NullPointerException e){
            return null;
        }
    }

    /**
     * Book search by keyword
     * @param keyword
     * @return the books which contain words having the stem of the keyword
     */
    public List<Book> getBooksByKeyword(String keyword) {
        ArrayList<Book> books = new ArrayList<>();
        String stem = keywordDictionary.getWord2stem().get(keyword.toLowerCase());
        if (stem == null) return books;
        HashMap<Integer, Double> keywordRelevance = keywordDictionary.getKeywordTable().get(stem);

        // sort the map by relevancy
        LinkedHashMap<Integer, Double> result = sortMapByRelevancy(keywordRelevance);

        for (int id : result.keySet()) {
            books.add(getBookById(id));
        }
        return books;
    }

    /**
     * search books containing text matching the regex given in parameter
     * @param regEx the regex to match in books' contenu
     * @return the books which contain text matching the regex given in parameter
     */
    public List<Book> getBooksByRegex(String regEx) {
        ArrayList<Book> books = new ArrayList<>();
        RegExTree ret;
        if (regEx.length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
            return books;
        } else {
            try {
                ret = RegEx.parse(regEx.toLowerCase());
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
                return books;
            }
        }

        NDFAutomaton ndfAutomaton = NDFAutomaton.regExTree2NFA(ret);
        DFAutomaton dfAutomaton = DFAutomaton.NFA2DFA(ndfAutomaton);

        HashMap<String, String> word2stem = keywordDictionary.getWord2stem();
        HashSet stems = new HashSet();
        for (String word : word2stem.keySet()) {
            if (dfAutomaton.search(word)) {
                String stem = word2stem.get(word);
                stems.add(stem);
                HashMap<Integer, Double> keywordRelevance = keywordDictionary.getKeywordTable().get(stem);
                // sort the map by relevancy
                LinkedHashMap<Integer, Double> result = sortMapByRelevancy(keywordRelevance);

                for (int id : result.keySet()) {
                    Book book = getBookById(id);
                    if (books.contains(book)) continue;
                    books.add(book);
                }
            }
        }
        return books;
    }

    /**
     * sort the books by Closeness Centrality
     * @param books
     * @return the list of books sorted by Closeness Centrality
     */
    public void sortBooksByCloseness(List<Book> books){
        List<Integer> orderedIds = new ArrayList<>(closenessCentrality.keySet());
        books.sort(Comparator.comparing(book -> orderedIds.indexOf(book.getId())));
    }

    /**
     * sort the books by relevancy of the text
     * @param map
     * @return the map sorted by relevancy
     * @param <T>
     */
    public <T> LinkedHashMap<T, Double> sortMapByRelevancy(Map<T, Double> map) {
        List<Map.Entry<T, Double>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<T, Double> result = new LinkedHashMap<>();
        for (Map.Entry<T, Double> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public List<Book> getNeighborBookByJaccard(int id){
        ArrayList<Book> books = new ArrayList<>();
        for (int neighborId : jaccardMatriceNeighbor.get(id)){
            books.add(getBookById(neighborId));
        }
        return books;
    }
}