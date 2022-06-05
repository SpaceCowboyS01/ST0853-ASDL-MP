package it.unicam.cs.asdl2122.mp2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 * 
 * @author Luca Tesei (template) - Simone Cisca simone.cisca@studeti.unicam.it (implementazione)
 *
 * @param <E>
 *                il tipo degli elementi degli insiemi disgiunti
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa a ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;
    
    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         * 
         * @param item
         *                 l'elemento conservato in questo nodo
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota d'insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
        currentElements = new HashMap<>();
    }

    @Override
    public boolean isPresent(E e) {
        if (e == null)
            throw new NullPointerException("e non può essere nullo.");

        return currentElements.containsKey(e);
    }

    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */
    @Override
    public void makeSet(E e) {
        if(e == null)
            throw new NullPointerException("Elemento nullo.");
        if (isPresent(e))
            throw new IllegalArgumentException("Elemento già presente.");

        currentElements.put(e,new Node<>(e));
    }

    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */
    @Override
    public E findSet(E e) {
        if (e == null)
            throw new NullPointerException("Elemento nullo.");
        if (!isPresent(e))
            return null;

        //Per leggibità creo una variabile
        Node<E> element = currentElements.get(e);

        if (!element.equals(element.parent))
            element.parent = currentElements.get(findSet(element.parent.item));

        return element.parent.item;
    }

    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */
    @Override
    public void union(E e1, E e2) {
        checkParameter(e1, e2);
        if (findSet(e1).equals(findSet(e2)))
            return;
/*
        //I due frammenti di codice sono equivalenti.
        //Questa versione non "funziona" correttamente
        E rep1 = currentElements.get(findSet(e1).parent.item;
        E rep2 = currentElements.get(findSet(e2).parent.item;

        if (currentElements.get(rep1).rank > currentElements.get(rep2).rank)
            currentElements.get(rep2).parent.item = rep1;
        else {
            currentElements.get(rep1).parent.item = rep2;
            if (currentElements.get(rep1).rank == currentElements.get(rep2).rank)
                currentElements.get(rep2).parent.rank++;
        }
 */
        Node<E> rep1 = currentElements.get(findSet(e1));
        Node<E> rep2 = currentElements.get(findSet(e2));
        if (rep1.rank > rep2.rank)
            rep2.parent = rep1;
        else {
            rep1.parent = rep2;
            if (rep1.rank == rep2.rank)
                rep2.rank++;
        }
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
        //Set di ritorno
        Set<E> toReturn = new HashSet<>();
        
        for (E element : currentElements.keySet())
            if (findSet(element).equals(element))
                toReturn.add(element);
        
        return toReturn;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        if (e == null)
            throw new NullPointerException("Elemento nullo.");
        if (!isPresent(e))
            throw new IllegalArgumentException("Elemento non presente.");

        //Rappresentante di e
        E eRepresentative = findSet(e);

        //Set di ritorno
        Set<E> toReturn = new HashSet<>();

        for (E element : currentElements.keySet())
            if (eRepresentative.equals(findSet(element)))
                toReturn.add(element);

        return toReturn;
    }

    @Override
    public void clear() {
        currentElements.clear();
    }

    private void checkParameter(E e1, E e2){
        if (e1 == null || e2 == null)
            throw new NullPointerException("Elementi nulli.");
        if (!isPresent(e1) || !isPresent(e2))
            throw new IllegalArgumentException("Elemento non presente.");
    }
}
