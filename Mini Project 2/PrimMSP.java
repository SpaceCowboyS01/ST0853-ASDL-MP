package it.unicam.cs.asdl2122.mp2;
//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

import java.util.*;

/**
 * Classe singleton che implementa l'algoritmo di Prim per trovare un Minimum
 * Spanning Tree di un grafo non orientato, pesato e con pesi non negativi.
 * 
 * L'algoritmo richiede l'uso di una coda di min priorità tra i nodi che può
 * essere realizzata con una semplice ArrayList (non c'è bisogno di ottimizzare
 * le operazioni d'inserimento, di estrazione del minimo, o di decremento della
 * priorità).
 * 
 * Si possono usare i colori dei nodi per registrare la scoperta e la visita
 * effettuata dei nodi.
 *
 * @author Luca Tesei (template)
 *     ** Simone Cisca simone.cisca@studeti.unicam.it ** (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class PrimMSP<L> {

    /*
     * In particolare: si deve usare una coda con priorità che può semplicemente
     * essere realizzata con una List<GraphNode<L>> e si deve mantenere un
     * insieme dei nodi già visitati
     */
    protected List<GraphNode<L>> queue;
    /**
     * Crea un nuovo algoritmo e inizializza la coda di priorità con una coda
     * vuota.
     */
    public PrimMSP() {
        queue = new ArrayList<>();
    }

    /**
     * Utilizza l'algoritmo goloso di Prim per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. Dopo l'esecuzione del metodo nei nodi del grafo il campo
     * previous deve contenere un puntatore a un nodo in accordo all'albero di
     * copertura minimo calcolato, la cui radice è il nodo sorgente passato.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @param s
     *              Il nodo del grafo g sorgente, cioè da cui parte il calcolo
     *              dell'albero di copertura minimo. Tale nodo sarà la radice
     *              dell'albero di copertura trovato
     * 
     * @throw NullPointerException
     *              se il grafo g o il nodo sorgente s sono nulli
     * @throw IllegalArgumentException
     *              se il nodo sorgente s non esiste in g
     * @throw IllegalArgumentException
     *              se il grafo g è orientato, non pesato o
     *              con pesi negativi
     */
    public void computeMSP(Graph<L> g, GraphNode<L> s) {
        checkParameters(g, s);

        for (GraphNode<L> element : g.getNodes()) {
            element.setColor(0);
            element.setFloatingPointDistance(Double.POSITIVE_INFINITY);
            element.setPrevious(null);
            queue.add(element);
        }

        s.setFloatingPointDistance(0);

        while(!queue.isEmpty()){
            GraphNode<L> nodeExtract = extractMin(queue);
            for (GraphNode<L> element : g.getAdjacentNodesOf(nodeExtract)) {
                if(queue.contains(element) &&
                        g.getEdge(element, nodeExtract).getWeight() < element.getFloatingPointDistance()){
                    element.setPrevious(nodeExtract);
                    element.setFloatingPointDistance(g.getEdge(element, nodeExtract).getWeight());
                    element.setColor(1);
                }
            }
        }
    }

    private GraphNode<L> extractMin(List<GraphNode<L>> l){
        GraphNode<L> toReturn = l.get(0);
        for (GraphNode<L> element : l)
            if(element.getFloatingPointDistance() < toReturn.getFloatingPointDistance())
                toReturn = element;

        l.remove(toReturn);
        toReturn.setColor(2);
        return toReturn;
    }

    /**
     *  Metodo per il controllo dei parametri
     * @param g - Grafo
     * @param s - Nodo
     */
    private void checkParams(Graph<L> g, GraphNode<L> s){
        if(g == null || s == null)
            throw new NullPointerException("Parametri nulli.");
        if (g.isDirected())
            throw new IllegalArgumentException("Grafo orientato.");
        if(g.getNode(s) == null)
            throw new IllegalArgumentException("Nodo non appartenente al grafo.");

        for (GraphEdge<L> element : g.getEdges()) {
            if (!element.hasWeight())
                throw new IllegalArgumentException(
                        "Tentativo di applicare l'algoritmo di Prim su un grafo" +
                                "con almeno un arco con peso non specificato");
            if (element.getWeight() < 0)
                throw new IllegalArgumentException(
                        "Tentativo di applicare l'algoritmo di Prim su un grafo" +
                                "con almeno un arco con peso negativo");
        }
    }
}
