package com.sorbonne.library_search_engine.service;

import com.sorbonne.library_search_engine.modele.Book;
import com.sorbonne.library_search_engine.utils.keywords.Keyword;
import com.sorbonne.library_search_engine.utils.keywords.KeywordDictionary;
import com.sorbonne.library_search_engine.utils.keywords.KeywordsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/25
 */
@Service
@Slf4j
public class InitIndexTableService {
    @Bean
    public KeywordDictionary keywordDictionary (Map<Integer, Book> library) throws IOException, ClassNotFoundException {
        if (new File("keywords.ser").exists()){
            log.info("Loading dictionary of keywords ...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keywords.ser"));
            KeywordDictionary dictionary = (KeywordDictionary) ois.readObject();
            ois.close();
            return dictionary;
        }

        HashMap<String, String> word2stem = new HashMap<>();
        HashMap<String, HashMap<Integer, Double>> keywordTable = new HashMap<>();
        HashMap<Integer, HashMap<String, Double>> keywordBookTable = new HashMap<>();
        String language;
        List<Keyword> keywords;
        for (Book book : library.values()) {
            int bookId = book.getId();
            String bookText = "books/" + bookId + ".txt";
            if (book.getLanguages().contains("en")) {
                language = "en";
            }
            else if (book.getLanguages().contains("fr")){
                language = "fr";
            }else {
                continue;
            }
            keywords = KeywordsExtractor.buildKeyWordsList(bookText, language);
            for (Keyword keyword : keywords) {
                String stem = keyword.getStem();
                Set<String> words = keyword.getWords();
                double relevance = keyword.getRelevance();

                // word2stem
                for (String word : words) {
                    word2stem.put(word, stem);
                }

                // keywordTable
                if (keywordTable.containsKey(stem)) {
                    keywordTable.get(stem).put(bookId, relevance);
                } else {
                    HashMap<Integer, Double> keywordRelevance = new HashMap<>();
                    keywordRelevance.put(bookId, relevance);
                    keywordTable.put(stem, keywordRelevance);
                }

                // keywordBookTable
                if (keywordBookTable.containsKey(bookId)) {
                    keywordBookTable.get(bookId).put(stem, relevance);
                } else {
                    HashMap<String, Double> stemRelevance = new HashMap<>();
                    stemRelevance.put(stem, relevance);
                    keywordBookTable.put(bookId, stemRelevance);
                }
            }
        }

        KeywordDictionary dictionary = new KeywordDictionary(word2stem, keywordTable, keywordBookTable);
        log.info("saving keywords.ser file");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("keywords.ser"));
        oos.writeObject(dictionary);
        oos.flush();
        oos.close();

        return dictionary;
    }


    public HashSet<String> splitWords(String text, String languageCode) throws IOException {
        Set<Character> alphabet = getAlphabet(languageCode);
        HashSet<String> words = new HashSet<>();
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < text.length(); i++) { //browsing the text, char by char
            char c = Character.toLowerCase(text.charAt(i));
            if (alphabet.contains(c)) { //if current char is in the alphabet (which is not a space, a point, etc)
                currentWord.append(c); //it is the next char of the current word
            } else {                   //else we have a word!
                String word = currentWord.toString();
                currentWord = new StringBuilder();
                if (!word.isEmpty() ) { //if the word is not empty
                    words.add(word);
                }
            }
        }
        String word = currentWord.toString();
        if (!word.isEmpty() ) { //if the word is not empty
            words.add(word);
        }
        return words;
    }

    public Set<Character> getAlphabet(String languageCode){
        Set<Character> alphabet = new HashSet<>();
        String filename = "language/" + languageCode + "/alphabet.txt";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String line = reader.readLine();
            while (line != null) {
                alphabet.add(line.charAt(0));
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return alphabet;
    }
}
