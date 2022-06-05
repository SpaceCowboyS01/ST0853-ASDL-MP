package it.unicam.cs.asdl2122.mp2;

import java.util.*;
// ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 * <p>
 * I nodi sono indicizzati da 0 a nodeCount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * a ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 * <p>
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa a ogni nodo l'indice assegnato in fase d'inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 * <p>
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i, j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 * <p>
 * Questa classe supporta i metodi di cancellazione di nodi e archi e
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase d'inserimento.
 *
 * @author Luca Tesei (template) - Simone Cisca simone.cisca@studeti.unicam.it (implementazione)
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    /*
     * Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
     * matrice di adiacenza
     */
    protected Map<GraphNode<L>, Integer> nodesIndex;

    /*
     * Matrice di adiacenza, gli elementi sono null od oggetti della classe
     * GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
     * dimensione gradualmente a ogni inserimento di un nuovo nodo e di
     * ridimensionarsi se un nodo viene cancellato.
     */
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<>();
        this.nodesIndex = new HashMap<>();
    }

    /*
     * nodesIndex è la mappa che contiene i nodi e il loro indice.
     * Essendo ripetuti un'unica volta all'interno della mappa, il metodo
     * size() restituirà il numero di nodi contenuti.
     */
    @Override
    public int nodeCount() {
        return nodesIndex.size();
    }

    @Override
    public int edgeCount() {
        int edgeCount = 0;

        for (ArrayList<GraphEdge<L>> toCount : matrix)
            for (GraphEdge<L> element : toCount)
                if (element != null)
                    edgeCount += 1;

        /*
         * Essendo un grafo non orientato, l'arco (u, v)
         * sarà presente sia nel "verso" u->v che in v->u.
         * Nel caso sia presente un arco (u, u), il numero di archi
         * sarebbe dispari e durante la divisione il cast a int troncherebbe
         * la parte decimale portando come risultato un arco in meno.
         * e.g. nodi a, b. archi a -> b, b -> a, b->b.
         *      dgeCount = 3; edgeCount / 2 = 1.5 -> cast ad int -> 1.
         */
        if (edgeCount % 2 == 1)
            edgeCount++;
        return edgeCount / 2;
    }

    //Per ripulire l'oggetto, utilizzo i costruttori di default su entrambi i suoi attributi
    @Override
    public void clear() {
        this.matrix = new ArrayList<>();
        this.nodesIndex = new HashMap<>();
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine d'inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(GraphNode<L> node) {
        if (node == null)
            throw new NullPointerException("Nodo nullo.");
        if (nodesIndex.containsKey(node)) return false;

        /*
         * Inserisco il nodo nella map, la sua value corrisponde al numero di nodi attuale
         * ovvero al suo indice
         */
        this.nodesIndex.put(node, nodeCount());

        // toInsert sarà la nuova riga della matrice, riempita da "null"
        ArrayList<GraphEdge<L>> toInsert = new ArrayList<>();
        for (int index = 0; index < nodeCount() - 1; index++)
            toInsert.add(null);
        matrix.add(toInsert);

        // Inserisco la nuova colonna a ogni riga
        for (int index = 0; index < nodeCount(); index++)
            matrix.get(index).add(null);

        // Anziché utilizzare una matrice quadrata, si poteva utilizzare una matrice triangolare.
        return true;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine d'inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(L label) {
        return addNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Nodo nullo.");
        if (!nodesIndex.containsKey(node))
            throw new IllegalArgumentException("Nodo non presente nel grafo.");

        //Set d'appoggio
        Set<GraphNode<L>> toSet = nodesIndex.keySet();

        //Servirà per tenere memorizzato value dopo la rimozione del nodo dall nodesIndex
        int toSetValue = nodesIndex.get(node);

        for (int index = 0; index < nodeCount(); index++)
            matrix.get(index).remove(toSetValue);
        matrix.remove(toSetValue);

        nodesIndex.remove(node);

        for (GraphNode<L> element : toSet)
            if (nodesIndex.get(element) > toSetValue) {
                nodesIndex.put(element, toSetValue);
                toSetValue++;
            }

    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(L label) {
        removeNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(int i) {
        if (i < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount() - 1)
            throw new IndexOutOfBoundsException("Valore maggiore di: " + nodeCount() + ".");

        GraphNode<L> node = matrix.get(i).get(i).getNode1();
        removeNode(node);
    }

    @Override
    public GraphNode<L> getNode(GraphNode<L> node) {
        if (node == null)
            throw new NullPointerException("Nodo nullo.");

        for (GraphNode<L> element : nodesIndex.keySet()) {
            if (element.equals(node)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public GraphNode<L> getNode(L label) {
        return getNode(new GraphNode<>(label));
    }

    @Override
    public GraphNode<L> getNode(int i) {
        if (i < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount() - 1)
            throw new IndexOutOfBoundsException("Valore maggiore del numero di nodi presenti.");

        return matrix.get(i).get(i).getNode1();
    }

    @Override
    public int getNodeIndexOf(GraphNode<L> node) {
        if (node == null)
            throw new NullPointerException("Nodo nullo.");

        for (GraphNode<L> tmp : nodesIndex.keySet())
            if (tmp.equals(node))
                return nodesIndex.get(tmp);

        //Se non verrà restituito nulla, node non sarà presente
        throw new IllegalArgumentException("Nodo non appartenente al grafo.");
    }

    @Override
    public int getNodeIndexOf(L label) {
        return getNodeIndexOf(new GraphNode<>(label));
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
        return nodesIndex.keySet();
    }

    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        if (edge == null)
            throw new NullPointerException("Arco nullo.");
        if (edge.isDirected())
            throw new IllegalArgumentException("L' arco incompatibile con il grafo.");
        if (!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2()))
            throw new IllegalArgumentException("L'arco non appartenente al grafo.");

        //Per leggibilità del codice, creo due variabili che assumeranno value dei nodi
        int indexOfNode1 = nodesIndex.get(edge.getNode1());
        int indexOfNode2 = nodesIndex.get(edge.getNode2());

        if (matrix.get(indexOfNode1).get(indexOfNode2) != null
                || matrix.get(indexOfNode2).get(indexOfNode1) != null)
            if (matrix.get(indexOfNode1).get(indexOfNode2).equals(edge)
                    || matrix.get(indexOfNode2).get(indexOfNode1).equals(edge))
                return false;

        matrix.get(indexOfNode1).set(indexOfNode2, edge);
        matrix.get(indexOfNode2).set(indexOfNode1, edge);
        return true;
    }

    @Override
    public boolean addEdge(GraphNode<L> node1, GraphNode<L> node2) {
        return addEdge(new GraphEdge<>(node1, node2, false));
    }

    @Override
    public boolean addEdge(L label1, L label2) {
        return addEdge((new GraphEdge<>(new GraphNode<>(label1), new GraphNode<>(label2), false)));
    }

    @Override
    public boolean addEdge(int i, int j) {
        return addWeightedEdge(i, j, Double.NaN);
    }

    @Override
    public boolean addWeightedEdge(L label1, L label2, double weight) {
        return addEdge((new GraphEdge<>(new GraphNode<>(label1), new GraphNode<>(label2), false, weight)));
    }

    @Override
    public boolean addWeightedEdge(GraphNode<L> node1, GraphNode<L> node2,
                                   double weight) {
        return addEdge(new GraphEdge<>(node1, node2, false, weight));
    }

    @Override
    public boolean addWeightedEdge(int i, int j, double weight) {
        if (i < 0 || j < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount() || j > nodeCount())
            throw new IndexOutOfBoundsException("Valore maggiore " + nodeCount() + ".");

        //Per leggibilità ho creato due variabili
        GraphNode<L> node1 = matrix.get(i).get(i).getNode1();
        GraphNode<L> node2 = matrix.get(j).get(j).getNode1();
        return addEdge((new GraphEdge<>(node1, node2, false, weight)));
    }

    @Override
    public void removeEdge(GraphEdge<L> edge) {
        if (edge == null)
            throw new NullPointerException("Nodo nullo.");
        if (edge.isDirected())
            throw new IllegalArgumentException("L' arco incompatibile con il grafo.");
        if (!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2()))
            throw new IllegalArgumentException("Arco passato non appartenente al grafo.");
        if (matrix.get(nodesIndex.get(edge.getNode1())).get(nodesIndex.get(edge.getNode2())) == null)
            throw new IllegalArgumentException("Arco passato non appartenente al grafo.");

        //Per leggibilità del codice, creo due variabili che assumeranno value dei nodi
        int indexOfNode1 = nodesIndex.get(edge.getNode1());
        int indexOfNode2 = nodesIndex.get(edge.getNode2());

        matrix.get(indexOfNode1).set(indexOfNode2, null);
        matrix.get(indexOfNode2).set(indexOfNode1, null);
    }

    @Override
    public void removeEdge(GraphNode<L> node1, GraphNode<L> node2) {
        removeEdge(new GraphEdge<>(node1, node2, false));
    }

    @Override
    public void removeEdge(L label1, L label2) {
        removeEdge(new GraphNode<>(label1), new GraphNode<>(label2));
    }

    @Override
    public void removeEdge(int i, int j) {
        if (i < 0 || j < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount() || j > nodeCount())
            throw new IndexOutOfBoundsException("Valore maggiore " + nodeCount() + ".");

        matrix.get(i).set(j, null);
        matrix.get(j).set(i, null);
    }

    @Override
    public GraphEdge<L> getEdge(GraphEdge<L> edge) {
        if (edge == null)
            throw new NullPointerException("Arco nullo.");
        if (edge.isDirected())
            return null;
        if (!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2()))
            throw new IllegalArgumentException("Arco inesistente nel grafo.");

        //Per leggibilità del codice, creo due variabili che assumeranno value dei nodi
        int indexNode1 = nodesIndex.get(edge.getNode1());
        int indexNode2 = nodesIndex.get(edge.getNode2());

        /*
         * Il metodo equals() della classe GraphEdge<>, nel caso l'arco non sia orientato,
         * ritorna true indipendentemente dall'ordine dei nodi
         */
        if (edge.equals(matrix.get(indexNode1).get(indexNode2)))
            return matrix.get(indexNode1).get(indexNode2);

        return null;
    }

    @Override
    public GraphEdge<L> getEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if (node1 == null || node2 == null)
            throw new NullPointerException("Nodo nullo.");
        if (!nodesIndex.containsKey(node1) || !nodesIndex.containsKey(node2))
            throw new IllegalArgumentException("Il nodo non appartiene al grafo.");

        return getEdge(new GraphEdge<>(node1, node2, false));
    }

    @Override
    public GraphEdge<L> getEdge(L label1, L label2) {
        return getEdge(new GraphEdge<>(new GraphNode<>(label1), new GraphNode<>(label2), false));
    }

    @Override
    public GraphEdge<L> getEdge(int i, int j) {
        if (i < 0 || j < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount() || j > nodeCount())
            throw new IndexOutOfBoundsException("Valore maggiore " + nodeCount() + ".");

        return matrix.get(i).get(j);
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        if (node == null)
            throw new NullPointerException("Nodo nullo.");
        if (!nodesIndex.containsKey(node))
            throw new IllegalArgumentException("Il nodo non appartiene al grafo.");

        // Set da ritornare
        Set<GraphNode<L>> toReturn = new HashSet<>();

        //Accedo alla riga del nodo
        for (GraphEdge<L> element : matrix.get(nodesIndex.get(node)))
            if (element != null) {
                if (element.getNode2().equals(node))
                    toReturn.add(element.getNode1());
                else
                    toReturn.add(element.getNode2());
            }
        return toReturn;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(L label) {
        return getAdjacentNodesOf(new GraphNode<>(label));
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(int i) {
        if (i < 0)
            throw new IndexOutOfBoundsException("I valori devono essere maggiori o uguali a 0.");
        if (i > nodeCount())
            throw new IndexOutOfBoundsException("I valori devono essere minori di " + nodeCount() + ".");

        return getAdjacentNodesOf(this.getNode(i));
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato.");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato.");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato.");
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        if (node == null)
            throw new NullPointerException("node non può essere nullo.");
        if (!nodesIndex.containsKey(node))
            throw new IllegalArgumentException("Il nodo non appartiene al grafo.");

        //Set da ritornare
        Set<GraphEdge<L>> toReturn = new HashSet<>();

        //Accedo alla riga del nodo
        for (GraphEdge<L> element : matrix.get(nodesIndex.get(node)))
            if (element != null)
                toReturn.add(element);

        return toReturn;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(L label) {
        return getEdgesOf(new GraphNode<>(label));
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(int i) {
        if (i < 0)
            throw new IndexOutOfBoundsException("Valore minore di 0.");
        if (i > nodeCount())
            throw new IndexOutOfBoundsException("Valore maggiore dei nodi presenti: " + nodeCount() + ".");

        Set<GraphEdge<L>> toReturn = new HashSet<>();

        for (GraphEdge<L> elementToAdd : matrix.get(i))
            if (elementToAdd!=null)
                toReturn.add(elementToAdd);

        return toReturn;
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> toReturn = new HashSet<>();


        for (int index = 0; index < nodeCount(); index++)
            for (GraphEdge<L> element : matrix.get(index))
                if (element != null)
                    toReturn.add(element);

        return toReturn;
    }
}