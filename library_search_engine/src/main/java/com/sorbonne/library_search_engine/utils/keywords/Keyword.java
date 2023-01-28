package com.sorbonne.library_search_engine.utils.keywords;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/19
 */
@Data
public class Keyword implements Comparable<Keyword>, Serializable {
    // Stem of word
    private String stem;
    // All words with this stem
    private Set<String> words;
    private Double relevance;

    public Keyword(String stem) {
        this.stem = stem;
        this.words = new HashSet<>();
        this.relevance = 0.0;
    }

    public void addWord(String term) {
        words.add(term);
        relevance++;
    }

    /**
     * We classify the words of the set according to their frequency in the text
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Keyword o) {
        // descending order
        return (o.getRelevance().compareTo(relevance));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        return stem.equals(keyword.stem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stem);
    }

    @Override
    public String toString() {
        return "Stem: '" + stem + '\'' +
                ", Relevance: " + relevance +
                ", Words: " + words.toString() +
                "\n";
    }
}
