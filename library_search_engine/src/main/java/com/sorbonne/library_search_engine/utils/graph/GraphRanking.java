package com.sorbonne.library_search_engine.utils.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/24
 */
public class GraphRanking {
    public static Double jaccardDistance(HashMap<String, Double> table1, HashMap<String, Double> table2) {
        double numerator = 0;
        double denominator = 0;
        HashMap<String, Double> tab2 = table2;
        for (String key : table1.keySet()) {
            double k1 = table1.get(key);
            double k2 = tab2.containsKey(key) ? tab2.get(key) : 0;
            numerator += Math.max(k1, k2) - Math.min(k1, k2);
            denominator += Math.max(k1, k2);
            if (tab2.containsKey(key)) tab2.remove(key);
        }
        for (String key : tab2.keySet()) {
            numerator += tab2.get(key);
            denominator += tab2.get(key);
        }
        return numerator/denominator;
    }
}
