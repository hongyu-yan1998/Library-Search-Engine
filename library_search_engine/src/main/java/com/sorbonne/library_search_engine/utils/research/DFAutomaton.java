package com.sorbonne.library_search_engine.utils.research;

import java.util.*;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2022/10/15 12:23
 */
public class DFAutomaton {
    private int[][] transitionTableDFA;
    private HashSet<Integer> finaux;

    public DFAutomaton(int[][] transitionTableDFA, HashSet<Integer> finaux) {
        this.transitionTableDFA = transitionTableDFA;
        this.finaux = finaux;
    }

    public static DFAutomaton NFA2DFA(NDFAutomaton ret) {
        int numEtat = 0;
        HashMap<Integer,ArrayList<Integer>> dfa = new HashMap<Integer,ArrayList<Integer>>();

        int[][] tTab = ret.getTransitionTable();
        ArrayList<Integer>[] eTab = ret.getEpsilonTransitionTable();
        int finalEtat = tTab.length-1;

        int[][] transitionTableDFA = new int[tTab.length][256];
        HashSet<Integer> finaux = new HashSet<>();
        //DUMMY VALUES FOR INITIALIZATION
        for (int i = 0; i < transitionTableDFA.length; i++) {
            for (int col = 0; col < 256; col++) {
                transitionTableDFA[i][col] = -1;
            }
        }

        // Obtenir l'etat initial
        ArrayList<Integer> etatInitial = new ArrayList<>(); // les etats avec les epsilon transitions
        etatInitial.add(0);
        etatInitial = elargirEtats(etatInitial, tTab, eTab);

        PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
        queue.add(numEtat);
        dfa.put(numEtat, etatInitial);
        if (etatInitial.contains(finalEtat)) finaux.add(numEtat);
        numEtat++;
        while(!queue.isEmpty()) {
            int num = queue.poll();
            ArrayList<Integer> etatsDeparts = dfa.get(num);
            for (int col = 0; col < 256; col++) {
                ArrayList<Integer> etatsArrives = new ArrayList<>();
                for(Integer etat : etatsDeparts) {
                    if (tTab[etat][col] != -1) {
                        etatsArrives.add(tTab[etat][col]);
                    }
                }
                // enleve les epsilon transitions pour les etats arrives
                etatsArrives = elargirEtats(etatsArrives, tTab, eTab);
                if (!etatsArrives.isEmpty()) {
                    if (dfa.containsValue(etatsArrives)) { // l'etat existe deja
                        transitionTableDFA[num][col] = getKey(dfa, etatsArrives);
                    } else { // Obtenir un nouveau etat
                        transitionTableDFA[num][col] = numEtat;
                        if (etatsArrives.contains(finalEtat)) finaux.add(numEtat);
                        queue.add(numEtat);
                        dfa.put(numEtat, etatsArrives);
                        numEtat++;
                    }
                }
            }
        }

        int[][] finalTransitionTableDFA = new int[dfa.size()][256];
        System.arraycopy(transitionTableDFA, 0, finalTransitionTableDFA, 0, dfa.size());
        String res = "nombre d'etats " + finalTransitionTableDFA.length +"\n";
        return new DFAutomaton(finalTransitionTableDFA, finaux);
    }

    public static ArrayList<Integer> elargirEtats(ArrayList<Integer> etats, int[][] tTab, ArrayList<Integer>[] eTab) {
        ArrayList<Integer> res = (ArrayList<Integer>) etats.clone();
        Queue<Integer> queue = new LinkedList<>(); // pour enlever les epsilon transitions
        queue.addAll(res);

        while(!queue.isEmpty()) {
            int sz = queue.size();
            for (int k = 0; k < sz; k++) {
                int cur = queue.poll();
                if(!eTab[cur].isEmpty()) {
                    res.addAll(eTab[cur]);
                    queue.addAll(eTab[cur]);
                }
            }
        }
        return res;
    }

    public static <K, V> K getKey(HashMap<K, V> map, V value) {
        for (K key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    //PRINT THE AUTOMATON TRANSITION TABLE
    public String toString() {
        String result="Initial state: 0\nFinal states: ";
        for (Integer f : finaux)
            result += f + " ";
        result += "\nTransition list:\n";
        for (int i = 0; i < transitionTableDFA.length; i++)
            for (int col = 0; col < 256; col++)
                if (transitionTableDFA[i][col] != -1)
                    result += "  " + i + " -- " + (char)col + " --> " + transitionTableDFA[i][col] + "\n";
        return result;
    }

    public boolean search(String word) {
        int word_length = word.length();
        if (word_length == 0) return false;
        int i = 0;
        int etatCourant = 0;
        int c = word.charAt(i);
        if(transitionTableDFA[etatCourant][c] != -1) {
            while(transitionTableDFA[etatCourant][c] != -1) {
                etatCourant = transitionTableDFA[etatCourant][c];
                i++;
                if (i >= word_length) break;
                c = word.charAt(i);
            }
            if (finaux.contains(etatCourant)) return true;
            else return search(word.substring(i));
        } else {
            while(transitionTableDFA[etatCourant][c] == -1) {
                i++;
                if (i >= word_length) return false;
                c = word.charAt(i);
            }
            return search(word.substring(i));
        }
    }
}
