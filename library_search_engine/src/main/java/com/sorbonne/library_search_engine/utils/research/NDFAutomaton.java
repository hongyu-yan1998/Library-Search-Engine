package com.sorbonne.library_search_engine.utils.research;

import java.util.ArrayList;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2022/10/15 10:57
 */
public class NDFAutomaton {
    private int[][] transitionTable; //ASCII transition
    private ArrayList<Integer>[] epsilonTransitionTable; //epsilon transition list
    public NDFAutomaton(int[][] transitionTable, ArrayList<Integer>[] epsilonTransitionTable) {
        this.transitionTable = transitionTable;
        this.epsilonTransitionTable = epsilonTransitionTable;
    }

    public int[][] getTransitionTable() {
        return transitionTable;
    }

    public ArrayList<Integer>[] getEpsilonTransitionTable() {
        return epsilonTransitionTable;
    }

    public static NDFAutomaton regExTree2NFA(RegExTree ret) {
        if(ret.subTrees.isEmpty()) {
            int[][] tTab = new int[2][256];
            ArrayList<Integer>[] eTab = new ArrayList[2];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            if (ret.root != RegEx.DOT)
                tTab[0][ret.root] = 1; //transition ret.root from initial state "0" to final state "1"
            else
                for (int i = 0; i < 256; i++)
                    tTab[0][i] = 1; //transition DOT from initial state "0" to final state "1"

            return new NDFAutomaton(tTab, eTab);
        }

        if (ret.root == RegEx.CONCAT) {
            NDFAutomaton gauche = regExTree2NFA(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = regExTree2NFA(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int len_g = tTab_g.length;
            int len_d = tTab_d.length;
            int[][] tTab = new int[len_g + len_d][256];
            ArrayList<Integer>[] eTab = new ArrayList[len_g + len_d];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();
            eTab[len_g - 1].add(len_g); //epsilon transition from old final state "left" to old initial state "right"

            //copy old transitions
            for (int i = 0; i < len_g; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = tTab_g[i][col];
            for (int i = 0; i < len_g; i++)
                eTab[i].addAll(eTab_g[i]);
            for (int i = len_g; i < len_g + len_d - 1; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_d[i - len_g][col] != -1)
                        tTab[i][col] = tTab_d[i - len_g][col] + len_g;
            for (int i = len_g; i < len_g + len_d - 1; i++)
                for (int s : eTab_d[i - len_g])
                    eTab[i].add(s + len_g);
            return new NDFAutomaton(tTab, eTab);
        } else if (ret.root == RegEx.ALTERN) {
            NDFAutomaton gauche = regExTree2NFA(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = regExTree2NFA(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int len_g = tTab_g.length;
            int len_d = tTab_d.length;
            int[][] tTab = new int[2 + len_g + len_d][256];
            ArrayList<Integer>[] eTab = new ArrayList[2 + len_g + len_d];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(1 + len_g); //epsilon transition from new initial state to old initial state
            eTab[1 + len_g - 1].add(2 + len_g + len_d - 1); //epsilon transition from old final state to new final state
            eTab[1 + len_g + len_d - 1].add(2 + len_g + len_d - 1); //epsilon transition from old final state to new final state

            for (int i = 1; i < 1 + len_g; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_g[i - 1][col] != -1) tTab[i][col] = tTab_g[i - 1][col] + 1; //copy old transitions
            for (int i = 1; i < 1 + len_g; i++) for (int s : eTab_g[i - 1]) eTab[i].add(s + 1); //copy old transitions
            for (int i = 1 + len_g; i < 1 + len_g + len_d - 1; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_d[i - 1 - len_g][col] != -1)
                        tTab[i][col] = tTab_d[i - 1 - len_g][col] + 1 + len_g; //copy old transitions
            for (int i = 1 + len_g; i < 1 + len_g + len_d; i++)
                for (int s : eTab_d[i - 1 - len_g]) eTab[i].add(s + 1 + len_g); //copy old transitions

            return new NDFAutomaton(tTab, eTab);
        } else if (ret.root == RegEx.ETOILE) {
            NDFAutomaton fils = regExTree2NFA(ret.subTrees.get(0));
            int[][] tTab_fils = fils.transitionTable;
            ArrayList<Integer>[] eTab_fils = fils.epsilonTransitionTable;
            int l = tTab_fils.length;
            int[][] tTab = new int[2+l][256];
            ArrayList<Integer>[] eTab = new ArrayList[2+l];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0; i<tTab.length; i++)
                for (int col=0; col<256; col++) tTab[i][col]=-1;
            for (int i=0; i<eTab.length; i++) eTab[i] = new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(2+l-1); //epsilon transition from new initial state to new final state
            eTab[2+l-2].add(2+l-1); //epsilon transition from old final state to new final state
            eTab[2+l-2].add(1); //epsilon transition from old final state to old initial state

            for (int i=1; i<2+l-1; i++)
                for (int col=0; col<256; col++)
                    if (tTab_fils[i-1][col]!=-1) tTab[i][col]=tTab_fils[i-1][col]+1; //copy old transitions
            for (int i=1; i<2+l-1; i++)
                for (int s: eTab_fils[i-1])
                    eTab[i].add(s+1); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }
        return null;
    }

    //PRINT THE AUTOMATON TRANSITION TABLE
    public String toString() {
        String result="Initial state: 0\nFinal state: "+(transitionTable.length-1)+"\nTransition list:\n";
        for (int i = 0; i < epsilonTransitionTable.length; i++)
            for (int state: epsilonTransitionTable[i])
                result += "  " + i + " -- epsilon --> " + state + "\n";
        for (int i = 0; i < transitionTable.length; i++)
            for (int col = 0; col < 256; col++)
                if (transitionTable[i][col] != -1)
                    result += "  " + i + " -- " + (char)col + " --> " + transitionTable[i][col] + "\n";
        return result;
    }
}
