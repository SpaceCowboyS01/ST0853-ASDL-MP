package it.unicam.cs.asdl2122.mp2;

import java.util.*;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * 
 * Classe singleton che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione d'insiemi
 * disgiunti di nodi del grafo.
 *
 * @author Luca Tesei (template)
 *     ** Simone Cisca simone.cisca@studeti.unicam.it ** (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class KruskalMSP<L> {

    /*
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private final ForestDisjointSets<GraphNode<L>> disjointSets;

    /*
     * Comparatore tra archi in base al peso.
     */
    private final EdgesComparator edgesComparator;

    /*
     * Classe interna per comparare due archi.
     */
    private class EdgesComparator implements Comparator<GraphEdge<L>>{
        @Override
        public int compare(GraphEdge<L> edge1, GraphEdge<L> edge2){
            return Double.compare(edge1.getWeight(), edge2.getWeight());
        }
    }

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<>();
        this.edgesComparator = new EdgesComparator();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     *
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     *
     * @throw NullPointerException se il grafo g è null
     *
     * @throw IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        if (g == null)
            throw new NullPointerException(
                    "Grafo nullo.");
        // Per la leggibilità, utilizzo un metodo per controllare i parametri
        checkParameter(g);

        // Set da ritornare
        Set<GraphEdge<L>> toReturn = new HashSet<>();

        disjointSets.clear();
        for (GraphNode<L> element : g.getNodes())
            disjointSets.makeSet(element);

        ArrayList<GraphEdge<L>> edgesInAscendingOrder = new ArrayList<>(g.getEdges());
        edgesInAscendingOrder.sort(edgesComparator);

        for (GraphEdge<L> element : edgesInAscendingOrder) {
            if (disjointSets.findSet(element.getNode1()) !=
                    disjointSets.findSet(element.getNode2())) {
                toReturn.add(element);
                disjointSets.union(element.getNode1(), element.getNode2());
            }
        }
        return toReturn;
    }

    /**
     * Controlla le tre proprietà richieste al grafo e, se non soddisfatte,
     * lancia le relative eccezioni
     *
     * @throw NullPointerException se il grafo g è null
     *
     * @throw IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    private void checkParameter(Graph<L> g){
        if (g.isDirected())
            throw new IllegalArgumentException("Grafo orientato.");

        for (GraphEdge<L> e : g.getEdges()) {
            if (!e.hasWeight())
                throw new IllegalArgumentException(
                        "Arco non pesato: " + e +".");
            if (e.getWeight() < 0)
                throw new IllegalArgumentException(
                        "Arco con peso negativo: " + e + ".");
        }
    }
}