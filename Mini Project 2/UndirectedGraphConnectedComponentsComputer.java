package it.unicam.cs.asdl2122.mp2;

import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singleton che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 *
 * @param <L> il tipo delle etichette dei nodi del grafo
 * @author Luca Tesei (template) - Simone Cisca simone.cisca@studeti.unicam.it (implementazione)
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione d'insiemi disgiunti.
     *
     * @param g un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     * insieme di nodi del grafo
     * @throws NullPointerException     se il grafo passato è nullo
     * @throws IllegalArgumentException se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        checkParameter(g);


        //Matrice da ritornare
        Set<Set<GraphNode<L>>> toReturn = new HashSet<>();
        f.clear();
        for (GraphNode<L> element : g.getNodes())
            f.makeSet(element);
        for (GraphEdge<L> element : g.getEdges())
            if (f.findSet(element.getNode1()) != f.findSet(element.getNode2()))
                f.union(element.getNode1(), element.getNode2());

        for (GraphNode<L> element : f.getCurrentRepresentatives())
            toReturn.add(f.getCurrentElementsOfSetContaining(element));

        return toReturn;
    }

    private void checkParameter(Graph<L> g) {
        if (g == null)
            throw new NullPointerException("Grafo nullo.");
        if (g.isDirected())
            throw new IllegalArgumentException("Grafo orientato.");
    }

}
