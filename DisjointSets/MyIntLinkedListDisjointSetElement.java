/**
 * Una semplice classe che implementa l'interface {@code DisjointSetElement} i
 * cui elementi sono interi e che è adatta per essere usata da una
 * implementazione dell'interface {@code DisjointSets} che usa liste
 * concatenate.
 * 
 * @author Luca Tesei
 *
 */
public class MyIntLinkedListDisjointSetElement implements DisjointSetElement {

    /*
     * L'intero che rappresenta questo elemento
     */
    private final int value;

    /*
     * Puntatore all'elemento rappresentante, cioè il primo della lista
     * concatenata che rappresenta l'insieme disgiunto di questo elemento
     */
    private DisjointSetElement representative;

    /*
     * Puntatore al prossimo elemento nella lista concatenata che rappresenta
     * l'insieme disgiunto di cui questo elemento fa parte
     */
    private DisjointSetElement nextElement;

    /*
     * Numero di elementi di questo insieme disgiunto, diverso da zero solo se
     * questo elemento è il rappresentante, altrimenti non viene usato
     */
    private int size;

    /**
     * Crea un nuovo elemento di un insieme disgiunto.
     * 
     * @param value
     *                  il numero associato al nuovo elemento
     */
    public MyIntLinkedListDisjointSetElement(int value) {
        this.value = value;
        /*
         * Metto i campi relativi al DisjointSet ai valori di default. Nelle API
         * dell'interface LinkedListDisjointSets è indicato che tali valori sono
         * quelli appropriati da mettere nel costruttore della classe che
         * implementa l'interface. Indicano che l'elemento appena creato non è
         * stato ancora inserito in nessun disjoint set.
         */
        this.representative = null;
        this.nextElement = null;
        this.size = 0;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Fornisce un riferimento a un altro elemento. Il significato associato
     * dipende dall'implementazione della interface {@DisjointSets} e viene
     * specificato in quella classe. Ad esempio in una implementazione con
     * Linked Lists questo riferimento può essere usato per restituire il
     * rappresentante dell'insieme disgiunto di questo elemento.
     *
     * @return un riferimento a un altro elemento, dipendente dal tipo di
     *         implementazione di {@DisjointSets}
     */
    @Override
    public DisjointSetElement getRef1() {
        return this.representative;
    }

    /**
     * Cambia il riferimento 1 di questo elemento. Si veda la API del metodo
     * {@getRef1}.
     *
     * @param e
     *              il nuovo valore del riferimento 1
     * @throws NullPointerException
     *                                  se l'elemento passato è null
     */
    @Override
    public void setRef1(DisjointSetElement e) {
        this.representative = e;
    }

    /**
     * Fornisce un riferimento a un altro elemento. Il significato associato
     * dipende dall'implementazione della interface {@DisjointSets} e viene
     * specificato in quella classe. Ad esempio in una implementazione con
     * Linked Lists questo riferimento può essere usato per restituire il
     * prossimo elemento nella lista concatenata che rappresenta l'insieme
     * disgiunto di cui questo elemento fa parte.
     *
     * @return un riferimento a un altro elemento, dipendente dal tipo di
     *         implementazione di {@DisjointSets}
     */
    @Override
    public DisjointSetElement getRef2() {
        return this.nextElement;
    }

    /**
     * Cambia il riferimento 2 di questo elemento. Si veda la API del metodo
     * {@getRef2}.
     *
     * @param e
     *              il nuovo valore del riferimento 1
     * @throws NullPointerException
     *                                  se l'elemento passato è null
     */
    @Override
    public void setRef2(DisjointSetElement e) {
        this.nextElement = e;
    }

    /**
     * Fornisce un numero intero associato a questo elemento. Il significato
     * associato dipende dall'implementazione della interface {@DisjointSets} e
     * viene specificato in quella classe. Ad esempio in una implementazione con
     * Linked Lists questo numero può essere associato al numero di elementi
     * correnti nell'insieme disgiunto se questo elemento ne è il
     * rappresentante.
     *
     * @return un intero associato a questo elemento, dipendente dal tipo di
     *         implementazione di {@DisjointSets}
     */
    @Override
    public int getNumber() {
        return this.size;
    }

    /**
     * Cambia l'intero associato a questo elemento. Si veda la API del metodo
     * {@getInt}.
     *
     * @param n
     *              il nuovo valore del riferimento 1
     */
    @Override
    public void setNumber(int n) {
        this.size = n;
    }

    /*
     * L'uguaglianza è definita in base al rappresentante e al valore, utile per
     * scrivere i test JUnit in maniera più semplice.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MyIntLinkedListDisjointSetElement))
            return false;
        MyIntLinkedListDisjointSetElement other = (MyIntLinkedListDisjointSetElement) obj;
        if (representative == null) {
            if (other.representative != null)
                return false;
        } else if (this.representative != other.representative)
            return false;
        if (this.value != other.value)
            return false;
        return true;
    }


    /*
     * Si usa solo il value per l'hashcode poiché l'uso di
     * representative.hashcode() potrebbe produrre delle chiamate ricorsive che
     * non terminano.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }
}
