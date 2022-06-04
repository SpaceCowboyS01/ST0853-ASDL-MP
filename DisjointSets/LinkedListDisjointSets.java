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

    /*
     * Nella rappresentazione con liste concatenate un elemento è presente in
     * qualche insieme disgiunto se il puntatore al suo elemento rappresentante
     * (ref1) non è null.
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
     * Nella rappresentazione con liste concatenate un nuovo insieme disgiunto è
     * rappresentato da una lista concatenata che contiene l'unico elemento. Il
     * rappresentante deve essere l'elemento stesso e la cardinalità deve essere
     * 1.
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
     * Nella rappresentazione con liste concatenate per trovare il
     * rappresentante di un elemento basta far riferimento al suo puntatore
     * ref1.
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
    @Override
    public void union(DisjointSetElement e1, DisjointSetElement e2) {
        if(e1 == null)
            throw new NullPointerException();
        if(e2 == null)
            throw new NullPointerException();
        if(e1.getRef1() == null || e2.getRef1() == null)
            throw new IllegalArgumentException();

        // Se e1 è il rappresentante di e2 allora fanno parte dello stesso insieme
        // il controllo viene eseguito anche da e2 su e1 inoltre entrambi potrebbero
        // non essere i rappresentanti dei loro insiemi ma avendo lo stesso
        // rappresentante vorrà dire che sono dello stesso insieme
        if(e2.getRef1() == e1 || e1.getRef1() == e2 || e1.getRef1() == e2.getRef1())
            return;

        // Se e1 è diverso dal suo rappresentante, lo sostituisco con quest'ultimo
        if(e1.getRef1() != e1)
            e1 = e1.getRef1();

        // Stesso procedimento per e2
        if(e2.getRef1() != e2)
            e2 = e2.getRef1();

        // Se il secondo elemento è più grande del primo, inverto le posizioni
        if(e1.getNumber() < e2.getNumber()){
            DisjointSetElement temp;
            temp = e1;
            e1 = e2;
            e2 = temp;
        }

        // Memorizzo l'elemento successivo a e1 in una variabile temporanea
        DisjointSetElement temp = e1.getRef2();
        int e1Number = e1.getNumber();
        int e2Number = e2.getNumber();

        // Memorizzo e2 come successivo di e1
        e1.setRef2(e2);
        DisjointSetElement elementoCorrente = e2;

        for( int i = e2Number; i > 0; i--){
            // Cambio la ref1 di ogni elemento con e1
            elementoCorrente.setRef1(e1);
            // Se il puntatore al prossimo elemento non è nullo passo all'elemento successivo
            if(elementoCorrente.getRef2() != null)
                elementoCorrente = elementoCorrente.getRef2();
        }

        // Azzero l'intero associato ad e2
        e2.setNumber(0);
        // L'ultimo elemento di e2 punterà all'elemento che prima era successivo ad e1
        elementoCorrente.setRef2(temp);
        //aggiorno l'intero di e1
        e1.setNumber(e1Number + e2Number);
    }

    @Override
    public Set<DisjointSetElement> getCurrentRepresentatives() {
        Set<DisjointSetElement> setDiRitorno = new HashSet<>();

        for(DisjointSetElement temp : mioHashSet)
            // Il rappresentate è l'unico elemento dell'insieme che punta se stesso
            if(temp.getRef1() == temp)
                setDiRitorno.add(temp);

        return setDiRitorno;
    }

    @Override
    public Set<DisjointSetElement> getCurrentElementsOfSetContaining(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();

        if(!(isPresent(e)))
            throw new IllegalArgumentException();

        Set<DisjointSetElement> setDiRitorno = new HashSet<>();
        DisjointSetElement elementoCorrente = e;

        // Prendo il rappresentante di elementoCorrente
        if(elementoCorrente.getRef1() != elementoCorrente )
            elementoCorrente = elementoCorrente.getRef1();

        // Scorro tutti gli elementi legati a temp
        for( int i = elementoCorrente.getNumber(); i > 0; i--){
            // Aggiungo l'elemento corrente a setDiRitorno
            setDiRitorno.add(elementoCorrente);
            elementoCorrente = elementoCorrente.getRef2();
        }
        return setDiRitorno;
    }

    @Override
    public int getCardinalityOfSetContaining(DisjointSetElement e) {
        if(e == null)
            throw new NullPointerException();
        if(e.getRef1() == null)
            throw new IllegalArgumentException();

        // Prendo il rappresentante dell'elemento e restituisco il suo valore
        return e.getRef1().getNumber();
    }
}