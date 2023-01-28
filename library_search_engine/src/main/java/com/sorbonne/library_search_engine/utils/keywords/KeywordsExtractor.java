package com.sorbonne.library_search_engine.utils.keywords;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/27
 */
public class KeywordsExtractor {

    /**
     * get the stem of the word
     * @param term
     * @return
     */
    public static String getStem(String term) throws IOException {
        TokenStream tokenStream = null;

        tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(term));
        tokenStream = new PorterStemFilter(tokenStream);

        Set<String> stems = new HashSet<String>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            stems.add(token.toString());
        }

        // We want only one stem
        if (stems.size() != 1) {
            return null;
        }
        String stem = stems.iterator().next();
        // if the stem has non-alphanumerical chars, return null
        if (!stem.matches("[a-zA-Z0-9-]+")) {
            return null;
        }

        if (tokenStream != null) {
            tokenStream.close();
        }

        return stem;
    }

    /**
     * Buld keyword list
     * @param bookText
     * @return
     * @throws FileNotFoundException
     */
    public static List<Keyword> buildKeyWordsList(String bookText, String language) throws IOException {
        TokenStream tokenStream = null;
        List<Keyword> keywords = new LinkedList<>();

        // We remove the english stop words (ignored words because they are too common)
        // We build more stop words on top of the default set (like gutenberg)
        String filename = "language/" + language + "/stopWords.txt";

        Scanner s = new Scanner(new File(filename));
        ArrayList<String> stopWordsList = new ArrayList<>();
        while(s.hasNextLine()) {
            stopWordsList.add(s.nextLine());
        }
        s.close();
        final CharArraySet stopWords = new CharArraySet(Version.LUCENE_40, stopWordsList, true);
        final CharArraySet defaultSet = language.equals("en") ? EnglishAnalyzer.getDefaultStopSet() : FrenchAnalyzer.getDefaultStopSet();
        stopWords.addAll(defaultSet);

        try {
            BufferedReader in = new BufferedReader(new FileReader(bookText));
            String text;
            while ((text = in.readLine()) != null) {
                // Hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
                text = text.replaceAll("-+", "-0");

                // Replace any punctuation char but apostrophes and dashes by a space
                text = text.replaceAll("[\\p{Punct}&&[^'-]]+", " ");

                // Replace most common english contractions
                text = text.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

                // tokenize text
                tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(text));

                // to lowercase
                tokenStream = new LowerCaseFilter(Version.LUCENE_40, tokenStream);

                // remove dots from acronyms (and "'s" but already done manually above)
                tokenStream = new ClassicFilter(tokenStream);

                // convert any char to ASCII
                tokenStream = new ASCIIFoldingFilter(tokenStream);

                tokenStream = new StopFilter(Version.LUCENE_40, tokenStream, stopWords);


                CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    String term = token.toString();
                    // Stem each term
                    String stem = getStem(term);
                    if (stem != null) {
                        // Create the keyword or get the existing one if any
                        Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
                        // Add its corresponding initial token
                        keyword.addWord(term.replaceAll("-0", "-"));
                    }
                }
            }
            Collections.sort(keywords);
            return keywords;
        }
        finally {
            if (tokenStream != null) {
                tokenStream.close();
            }
        }
    }

    public static <T> T find(Collection<T> collection, T example) {
        for (T element : collection) {
            if (element.equals(example)) {
                return element;
            }
        }
        collection.add(example);
        return example;
    }
}
