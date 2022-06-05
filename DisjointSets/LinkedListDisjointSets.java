import java.util.HashSet;
import java.util.Set;

/**
 * @author Luca Tesei (template) **Simone Cisca, simone.cisca@studenti.unicam.it** (implementazione)
 *
 * Implementazione:
 * LinkedListDisjointSets utilizza un HashSet di tipo {@link DisjointSetElement} in quanto non
 * permette la duplicazione degli elementi.
 *
 */
public class LinkedListDisjointSets implements DisjointSets {

    //Attributi
    private HashSet<DisjointSetElement> mioHashSet;

    //Metodi
    //Crea una collezione vuota di insiemi disgiunti.
    public LinkedListDisjointSets() {
        mioHashSet = new HashSet<>();
    }


    /**
     * Determina se un elemento è stato precedentemente inserito.
     *
     * @param e
     *              l'elemento da cercare
     * @return true se l'elemento <code>e</code> è già presente in qualche
     *         insieme disgiunto corrente, false altrimenti
     */
    @Override
    public boolean isPresent(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();

        if(e.getRef1() == null)
            return false;
        else
            return true;
    }
    /*
     * Nella rappresentazione con liste concatenate un elemento è presente in
     * qualche insieme disgiunto se il puntatore al suo elemento rappresentante
     * (ref1) non è null.
     */

    /**
     * Crea un nuovo insieme disgiunto contenente solo l'elemento dato.
     *
     * @param e
     *              l'elemento da inserire nell'insieme creato
     * @throws NullPointerException
     *                                      se l'elemento passato è null
     * @throws IllegalArgumentException
     *                                      se l'elemento passato è già presente
     *                                      in uno degli insiemi disgiunti
     *                                      correnti
     */
    @Override
    public void makeSet(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();

        if(isPresent(e))
            throw new IllegalArgumentException();

        e.setRef1(e);
        e.setNumber(1);
        mioHashSet.add(e);
    }
    /*
     * Nella rappresentazione con liste concatenate un nuovo insieme disgiunto è
     * rappresentato da una lista concatenata che contiene l'unico elemento. Il
     * rappresentante deve essere l'elemento stesso e la cardinalità deve essere
     * 1.
     */

    /**
     * Restituisce il rappresentante dell'insieme disgiunto in cui si trova
     * l'elemento passato.
     *
     * @param e
     *              l'elemento di cui cercare l'insieme disgiunto
     * @return l'elemento rappresentante dell'insieme disgiunto in cui
     *         attualmente si trova <code>e</code>
     * @throws NullPointerException
     *                                      se l'elemento passato è null
     * @throws IllegalArgumentException
     *                                      se l'elemento passato non è presente
     *                                      in nessuno degli insiemi disgiunti
     *                                      correnti
     *
     */
    @Override
    public DisjointSetElement findSet(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();

        if(!(isPresent(e)))
            throw new IllegalArgumentException();

        // Ritorno ref1 di "e"
        return e.getRef1();
    }
    /*
     * Nella rappresentazione con liste concatenate per trovare il
     * rappresentante di un elemento basta far riferimento al suo puntatore
     * ref1.
     */

    /**
     * Unisce gli insiemi disgiunti di cui fanno parte i due elementi passati.
     * Se gli elementi passati fanno già parte dello stesso insieme non fa
     * nulla. Dopo l'operazione il rappresentante dell'insieme unito è un
     * elemento dell'insieme disgiunto la cui identità è definita dalla classe
     * che implementa questa interface.
     *
     * @param e1
     *               un elemento del primo insieme da unire
     * @param e2
     *               un elemento del secondo insieme da unire
     * @throws NullPointerException
     *                                      se almeno uno dei due elementi
     *                                      passati è null
     * @throws IllegalArgumentException
     *                                      se almeno uno dei due elementi
     *                                      passati non è presente in nessuno
     *                                      degli insiemi disgiunti correnti
     */
    @Override
    public void union(DisjointSetElement e1, DisjointSetElement e2) {
        checkParameters(e1,e2);

        // Se e1 è il rappresentante di e2 allora fanno parte dello stesso insieme
        // il controllo viene eseguito anche da e2 su e1 inoltre entrambi potrebbero
        // non essere i rappresentanti dei loro insiemi ma avendo lo stesso
        // rappresentante vorrà dire che sono dello stesso insieme
        if(e2.getRef1() == e1 || e1.getRef1() == e2 || e1.getRef1() == e2.getRef1())
            return;

        // Memorizzo l'elemento successivo a e1 in una variabile temporanea
        DisjointSetElement temp = e1.getRef2();
        int e1Number = e1.getNumber();
        int e2Number = e2.getNumber();

        // Memorizzo e2 come successivo di e1
        e1.setRef2(e2);
        DisjointSetElement currentItem = e2;

        for( int i = e2Number; i > 0; i--){
            // Cambio la ref1 di ogni elemento con e1
            currentItem.setRef1(e1);
            // Se il puntatore al prossimo elemento non è nullo passo all'elemento successivo
            if(currentItem.getRef2() != null)
                currentItem = currentItem.getRef2();
        }

        // Azzero l'intero associato ad e2
        e2.setNumber(0);
        // L'ultimo elemento di e2 punterà l'elemento che prima era il successivo di e1
        currentItem.setRef2(temp);
        //aggiorno l'intero di e1
        e1.setNumber(e1Number + e2Number);
    }
    /*
     * Dopo l'unione di due insiemi effettivamente disgiunti il rappresentante
     * dell'insieme unito è il rappresentate dell'insieme che aveva il numero
     * maggiore di elementi tra l'insieme di cui faceva parte {@code e1} e
     * l'insieme di cui faceva parte {@code e2}. Nel caso in cui entrambi gli
     * insiemi avevano lo stesso numero di elementi il rappresentante
     * dell'insieme unito è il rappresentante del vecchio insieme di cui faceva
     * parte {@code e1}.
     *
     * Questo comportamento è la risultante naturale di una strategia che
     * minimizza il numero di operazioni da fare per realizzare l'unione nel
     * caso di rappresentazione con liste concatenate.
     */

    /**
     * Restituisce l'insieme dei rappresentantanti degli insiemi disgiunti
     * attualmente presenti.
     *
     * @return l'insieme corrente dei rappresentanti degli insiemi disgiunti
     */
    @Override
    public Set<DisjointSetElement> getCurrentRepresentatives() {
        Set<DisjointSetElement> toReturn = new HashSet<>();

        for(DisjointSetElement toScroll : mioHashSet)
            // Il rappresentate è l'unico elemento dell'insieme che punta se stesso
            if(toScroll.getRef1() == toScroll)
                toReturn.add(toScroll);

        return toReturn;
    }

    /**
     * Restituisce gli elementi appartenenti all'insieme disgiunto di cui fa
     * parte un certo elemento.
     *
     * @param e
     *              l'elemento di cui si vuole ottenere l'insieme disgiunto di
     *              cui fa parte
     * @return l'insieme di elementi di cui fa parte l'elemento passato
     * @throws NullPointerException
     *                                      se l'insieme passato è null
     * @throws IllegalArgumentException
     *                                      se l'elemento passato non è
     *                                      contenuto in nessun insieme
     *                                      disgiunto
     */
    @Override
    public Set<DisjointSetElement> getCurrentElementsOfSetContaining(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();

        if(!(isPresent(e)))
            throw new IllegalArgumentException();

        Set<DisjointSetElement> toReturn = new HashSet<>();
        DisjointSetElement currentItem = e;

        // Prendo il rappresentante di elementoCorrente
        if(currentItem.getRef1() != currentItem )
            currentItem = currentItem.getRef1();

        // Scorro tutti gli elementi legati a temp
        for( int i = currentItem.getNumber(); i > 0; i--){
            // Aggiungo l'elemento corrente a setDiRitorno
            toReturn.add(currentItem);
            currentItem = currentItem.getRef2();
        }
        return toReturn;
    }

    /**
     * Restituisce la cardinalità dell'insieme disgiunto di cui fa parte un
     * certo elemento.
     *
     * @param e
     *              l'elemento di cui si vuole ottenere la cardinalità
     * @return il numero di elementi di cui fa parte l'elemento passato
     * @throws NullPointerException
     *                                      se l'insieme passato è null
     * @throws IllegalArgumentException
     *                                      se l'elemento passato non è
     *                                      contenuto in nessun insieme
     *                                      disgiunto
     */
    @Override
    public int getCardinalityOfSetContaining(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();
        if(e.getRef1() == null)
            throw new IllegalArgumentException();

        // Prendo il rappresentante dell'elemento e restituisco il suo number
        return e.getRef1().getNumber();
    }

    /**
     *  Metodo per il controllo dei parametri
     * @param e1
     * @param e2
     */
    private void checkParameters(DisjointSetElement e1, DisjointSetElement e2){
        if(e1 == null)
            throw new NullPointerException();
        if(e2 == null)
            throw new NullPointerException();
        if(e1.getRef1() == null || e2.getRef1() == null)
            throw new IllegalArgumentException();

        // Prendo il rappresentate da entrambi i DisjointSetElement
        if(e1.getRef1() != e1)
            e1 = e1.getRef1();
        if(e2.getRef1() != e2)
            e2 = e2.getRef1();

        exchangeOfParameters(e1, e2);
    }

    /**
     * Metodo per invertire i parametri
     * @param e1
     * @param e2
     */
    private void exchangeOfParameters(DisjointSetElement e1, DisjointSetElement e2){
        // Se il secondo elemento è più grande del primo, inverto le posizioni
        if(e1.getNumber() < e2.getNumber()){
            DisjointSetElement temp;
            temp = e1;
            e1 = e2;
            e2 = temp;
        }
    }
}