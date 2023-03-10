package com.sorbonne.library_search_engine.utils.graph;

import com.sorbonne.library_search_engine.utils.keywords.KeywordDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/24
 */
@Slf4j
@Component
public class GraphRanking {
    @Autowired
    private KeywordDictionary keywordDictionary;

    /**
     * calculate the jaccard distance between the index tables
     * @param table1 first index table
     * @param table2 second index table
     * @return the jaccard distance between the index tables
     */
    public Double calculateJaccardDistance(HashMap<String, Double> table1, HashMap<String, Double> table2) {
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

    /**
     * build the geometric graph with the jaccard distance
     * @return The distance of each document from other documents
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public HashMap<Integer, HashMap<Integer, Double>> buildJaccardMatrice() throws IOException, ClassNotFoundException {
        HashMap<Integer, HashMap<Integer, Double>> jaccardMatrice = new HashMap<>();
        if (new File("jaccard.ser").exists()){
            log.info("Loading Jaccard distance matrice ...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("jaccard.ser"));
            jaccardMatrice = (HashMap<Integer, HashMap<Integer, Double>>) ois.readObject();
            ois.close();
            return jaccardMatrice;
        }

        log.info("Start Jaccard distance matrice ...");
        HashMap<Integer, HashMap<String, Double>> keywordBookTable = keywordDictionary.getKeywordBookTable();
        Set<Integer> ids = keywordBookTable.keySet();
        for (int id1 : ids) {
            for (int id2 : ids) {
                if (id1 == id2) continue;
                if (jaccardMatrice.containsKey(id1) && jaccardMatrice.get(id1).containsKey(id2)) continue;
                double distance = calculateJaccardDistance(keywordBookTable.get(id1), keywordBookTable.get(id2));

                if (jaccardMatrice.containsKey(id1)) {
                    jaccardMatrice.get(id1).put(id2, distance);
                } else {
                    HashMap<Integer, Double> distancesDoc1 = new HashMap<>();
                    distancesDoc1.put(id2, distance);
                    jaccardMatrice.put(id1, distancesDoc1);
                }

                if (jaccardMatrice.containsKey(id2)) {
                    jaccardMatrice.get(id2).put(id1, distance);
                } else {
                    HashMap<Integer, Double> distancesDoc2 = new HashMap<>();
                    distancesDoc2.put(id1, distance);
                    jaccardMatrice.put(id2, distancesDoc2);
                }
            }
        }

        log.info("saving jaccard.ser file");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("jaccard.ser"));
        oos.writeObject(jaccardMatrice);
        oos.flush();
        oos.close();

        return jaccardMatrice;
    }

    /**
     *
     * @return the book id and its jaccard neighbors
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Bean
    public HashMap<Integer, ArrayList<Integer>> jaccardMatriceNeighbor() throws IOException, ClassNotFoundException {
        HashMap<Integer, ArrayList<Integer>> jaccardMatriceNeighbor = new HashMap<>();
        if (new File("jaccardNeighbor.ser").exists()){
            log.info("Loading Jaccard matrice neighbor ...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("jaccardNeighbor.ser"));
            jaccardMatriceNeighbor = (HashMap<Integer, ArrayList<Integer>>) ois.readObject();
            ois.close();
            return jaccardMatriceNeighbor;
        }

        log.info("Start building Jaccard matrice neighbor ...");
        HashMap<Integer, HashMap<Integer, Double>> jaccardMatrice = buildJaccardMatrice();
        Set<Integer> ids = jaccardMatrice.keySet();
        for (int id : ids) {
            HashMap<Integer, Double> map = jaccardMatrice.get(id);
            map.entrySet().removeIf(m -> m.getValue() > 0.8);
            // sort the map
            List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(map.entrySet());
            entryList.sort(Comparator.comparing(Map.Entry::getValue));
            ArrayList<Integer> result = new ArrayList<>();
            for (Map.Entry<Integer, Double> entry : entryList) {
                result.add(entry.getKey());
            }
            jaccardMatriceNeighbor.put(id, result);
        }

        log.info("saving jaccardNeighbor.ser file");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("jaccardNeighbor.ser"));
        oos.writeObject(jaccardMatriceNeighbor);
        oos.flush();
        oos.close();

        return jaccardMatriceNeighbor;
    }

    /**
     *
     * @return the map of closeness centrality
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Bean
    public LinkedHashMap<Integer, Double> closenessCentrality() throws IOException, ClassNotFoundException {
        LinkedHashMap<Integer, Double> closenessMap = new LinkedHashMap<>();
        if (new File("closeness.ser").exists()){
            log.info("Loading Closeness Centrality Ranking ...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("closeness.ser"));
            closenessMap = (LinkedHashMap<Integer, Double>) ois.readObject();
            ois.close();
            return closenessMap;
        }

        log.info("Start building Closeness Centrality Ranking ...");
        HashMap<Integer, HashMap<Integer, Double>> jaccardMatrice = buildJaccardMatrice();
        int nbBooks = jaccardMatrice.size();
        for (int id : jaccardMatrice.keySet()) {
            double sum = 0;
            for (double value : jaccardMatrice.get(id).values()) {
                sum += value;
            }
            closenessMap.put(id, (nbBooks-1)/sum);
        }

        // sort the map
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(closenessMap.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<Integer, Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }

        log.info("saving closeness.ser file");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("closeness.ser"));
        oos.writeObject(result);
        oos.flush();
        oos.close();

        return result;
    }
}
