package com.sorbonne.library_search_engine;

import com.sorbonne.library_search_engine.utils.graph.GraphRanking;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LibrarySearchEngineApplicationTests {

    private LinkedHashMap<Integer, Double> closenessCentrality;

    @Test
    void contextLoads() {
    }

    @Test
    void testJaccardDistance() throws IOException {
        HashMap<String, Double> table1 = new HashMap<>();
        HashMap<String, Double> table2 = new HashMap<>();
        table1.put("Sargon", 3.0);
        table1.put("Babylon", 5.0);
        table1.put("lucy", 2.0);
        table2.put("Sargon", 2.0);
        table2.put("Babylon", 3.0);
        table2.put("soi", 1.0);
        System.out.println(GraphRanking.calculateJaccardDistance(table1, table2));
    }

}
