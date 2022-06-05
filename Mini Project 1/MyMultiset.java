import java.util.*;

/**
 * @author Luca Tesei (template)
 *       **Simone Cisca - simone.cisca@studenti.unicam.it** (implementazione)
 *
 * Implementazione:
 * MyMultiset utilizza una HashMap con chiave "E" e valore Integer.
 * Si è preferito utilizzare questa struttura dati in quanti l'esecuzione dei principali metodi
 * viene svolta in O (o grande) di 1.
 * La chiave è l' hash dell'oggetto E(azione interna dei metodi di HashMap), garantendo l'accesso diretto ai valori richiesti.
 * I valori Integer rappresentano le occorrenze di ogni elemento.
 *
 * @param <E> il tipo degli elementi del multiset
 */
public class MyMultiset<E> implements Multiset<E> {

    // Attributi della classe

    // mioHashMap è un oggetto di tipo HashMap, conterrà gli elementi del MultiSet.
    private HashMap<E, Integer> mioHashMap;
    // Ad ogni azione di add, remove o setCount verrà incrementato.
    // Servirà all' iteratore, per controllare che non vengano effettuate modifiche durante l'iterazione.
    private int modificheEffettuate;
    // Intero che indica gli elementi presenti nel multiSet, tenendo conto delle occorrenze
    private int elementiPresenti;

    // Classe Iteratore
    private class Itr implements Iterator<E> {
        private final Iterator<E> iteratoreChiavi = elementSet().iterator();
        private final int modificheAttese = modificheEffettuate;

        private E elementoCorrente;
        private int occorrenzeElementoCorrente;
        private Itr(){
            elementoCorrente = null;
            occorrenzeElementoCorrente = 0;
        }
        // L'ultimo elemento dell'iteratore potrebbe avere più di un occorrenza
        // quindi controllo che occorrenzeElementoCorrente sia maggiore di 0
        @Override
        public boolean hasNext() {
            return iteratoreChiavi.hasNext() || occorrenzeElementoCorrente > 0;
        }

        @Override
        public E next() {
            if (modificheAttese != modificheEffettuate)
                // Il multiset è stato modificato durante l'iterazione
                throw new ConcurrentModificationException("L'iteratore è stato modificato");

            // Le occorrenze dell'elemento attuale sono finite, quindi
            // si può passare all'elemento successivo
            if(occorrenzeElementoCorrente == 0) {
                elementoCorrente = iteratoreChiavi.next();
                occorrenzeElementoCorrente = mioHashMap.get(elementoCorrente);
            }
            occorrenzeElementoCorrente--;
            return elementoCorrente;
        }
    }

    /**
     * Crea un multiset vuoto.
     */
    public MyMultiset() {
        mioHashMap = new HashMap<>();
        modificheEffettuate = 0;
        elementiPresenti = 0;
    }
    /**
     * Restituisce il numero totale di elementi in questo multinsieme. Ad
     * esempio, per il multinsieme {@code [1,2,3,1,4]} il metodo restituisce
     * {@code 5} poiché l'elemento {@code 1} ha due occorrenze.
     *
     * @return  elementiPresenti
     *
     *          il numero totale di elementi in questo multinsieme contando tutte le occorrenze
     */
    @Override
    public int size() {
        return elementiPresenti;
    }

    /**
     * Restituisce il numero di occorrenze di un certo elemento in questo
     * multinsieme.
     *
     * @param element
     *                    l'elemento di cui contare le occorrenze
     * @return il numero di occorrenze dell'elemento {@code element} in questo
     *         multinsieme. Se l'elemento non è presente restituisce 0
     * @throws NullPointerException
     *                                  se {@code element} è null
     */
    @Override
    public int count(Object element) {
        if (element == null)
            throw new NullPointerException();
        // Alla variabile locale "val" di tipo Integer assegno il valore corrispondente all'elemento.
        Integer val = mioHashMap.get(element);
        // Restituisco 0 se l'elemento è nullo, altrimenti val.
        return val == null ? 0 : val;
    }

    /**
     * Aggiunge un numero di occorrenze di un certo elemento a questo multiset.
     *
     * @param element
     *                        l'elemento di cui aggiungere le occorrenze
     * @param occurrences
     *                        il numero di occorrenze dell'elemento da
     *                        aggiungere. Può essere zero, nel qual caso non
     *                        verrà apportata alcuna modifica.
     * @return il numero di occorrenze dell'elemento prima dell'operazione;
     *         possibilmente zero
     * @throws IllegalArgumentException
     *                                      se {@code occurrences} è negativo, o
     *                                      se questa operazione comporterebbe
     *                                      più di {@code Integer.MAX_VALUE}
     *                                      occorrenze dell'elemento
     * @throws NullPointerException
     *                                      se {@code element} è null
     */
    @Override
    public int add(E element, int occurrences) {
        if (element == null)
            throw new NullPointerException("L'elemento non può essere nullo");
        if (occurrences < 0 || occurrences > Integer.MAX_VALUE)
            // occurrences > Integer.MAX_VALUE è un controllo
            // ridondante in quanto se occurrences è maggiore, non può essere un int
            throw new IllegalArgumentException("L'occorrenza non può essere minore di 0 o maggiore di " + Integer.MAX_VALUE);

        Integer occorrenzePrecedenti = mioHashMap.get(element);
        // Se il valore di get(Element) è null vuol dire che non è presente nella Map
        // quindi a occorrenzePrecedenti assegno 0
        occorrenzePrecedenti = occorrenzePrecedenti == null ? 0 : occorrenzePrecedenti;

        // la differenza tra Integer.MAX_VALUE e occorrenzePrecedenti
        // da come risultato il numero di occorenze che possono ancora essere inserite
        if (Integer.MAX_VALUE - occorrenzePrecedenti < occurrences)
            throw new IllegalArgumentException("L'occorrenza non può essere minore di 0 o maggiore di " + Integer.MAX_VALUE);

        // tramite il metodo put scrivo, o sovrascrivo, le occorrenze di element
        mioHashMap.put(element, occorrenzePrecedenti + occurrences);

        // Incremento le modifiche e aggiorno il numero di elementi presenti.
        modificheEffettuate++;
        elementiPresenti += occurrences;
        return occorrenzePrecedenti;
    }

    /**
     * Aggiunge una singola occorrenza di un certo elemento a questo multiset.
     *
     * @param element
     *                        l'elemento di cui aggiungere l'occorrenza
     * @throws IllegalArgumentException
     *                                      se questa operazione comporterebbe
     *                                      più di {@code Integer.MAX_VALUE}
     *                                      occorrenze dell'elemento
     * @throws NullPointerException
     *                                      se {@code element} è null
     */
    @Override
    public void add(E element) {
        add(element, 1);
    }

    /**
     * Rimuove da questo multinsieme un dato numero di occorrenze di un elemento
     * se questo è presente secondo il metodo {@code boolean contains(Object)}.
     * Se il multinsieme contiene meno del dato numero di occorrenze, tutte le
     * occorrenze verranno rimosse.
     *
     * @param element
     *                        l'elemento di cui rimuovere le occorrenze
     * @param occurrences
     *                        il numero di occorrenze dell'elemento da
     *                        rimuovere. Può essere zero, nel qual caso non
     *                        verrà apportata alcuna modifica
     * @return il numero di occorrenze dell'elemento prima dell'operazione;
     *         possibilmente zero
     * @throws IllegalArgumentException
     *                                      se {@code occurrences} è negativo
     * @throws NullPointerException
     *                                      se {@code element} è null
     */
    @Override
    public int remove(Object element, int occurrences) {
        if (element == null)
            throw new NullPointerException("L'elemento non può essere nullo");
        if (occurrences < 0 || occurrences > Integer.MAX_VALUE)
            throw new IllegalArgumentException("L'occorrenza non può essere minore di 0 o maggiore di " + Integer.MAX_VALUE);

        E elementoDaAggiungere = (E) element;
        // Se occcorrenzePrecedenti è null vuol dire che l'oggetto non è presente nella Map
        Integer occorrenzePrecedenti = mioHashMap.get(elementoDaAggiungere);
        if (occorrenzePrecedenti == null)
            return 0;

        mioHashMap.put(elementoDaAggiungere, occorrenzePrecedenti - occurrences);

        // Se le occorrenze dell'elemento sono minori o uguali a 0, l'elemento viene rimosso dalla Map
        if (mioHashMap.get(elementoDaAggiungere) <= 0)
            mioHashMap.remove(elementoDaAggiungere);

        // Incremento le modifiche e aggiorno il numero di elementi presenti.
        if(occurrences != 0)
            modificheEffettuate++;
        elementiPresenti -= occurrences;
        return occorrenzePrecedenti;
    }

    /**
     * Rimuove da questo multinsieme una singola occorrenza di un elemento se
     * questo è presente secondo il metodo {@code boolean contains(Object)}.
     *
     * @param element
     *                    l'elemento di cui rimuovere l'occorrenza
     * @return {@code true} se un'occorrenza è stata trovata e rimossa,
     *         {@code false altrimenti}
     * @throws NullPointerException
     *                                  se {@code element} è null
     */
    @Override
    public boolean remove(Object element) {
        return remove(element, 1) > 0;
    }

    /**
     * Aggiunge o rimuove le occorrenze di un elemento in modo tale che il
     * l'elemento raggiunga il numero di occorrenze desiderato.
     *
     * @param element
     *                        l'elemento di cui aggiungere o togliere occorrenze
     * @param occurrences
     *                        il numero desiderato di occorrenze dell'elemento
     * @return il numero di occorrenze dell'elemento prima dell'operazione;
     *         possibilmente zero
     * @throws IllegalArgumentException
     *                                      se {@code count} è negativo
     * @throws NullPointerException
     *                                      se {@code element} è nullo
     */
    @Override
    public int setCount(E element, int count) {
        if (element == null)
            throw new NullPointerException("");
        if (count < 0 || count > Integer.MAX_VALUE)
            throw new IllegalArgumentException("L'occorrenza non può essere minore di 0 o maggiore di " + Integer.MAX_VALUE);

        // Ad occorrenzePrecedenti assegno 0 se l'elemento non era presente nel this.mioHashMap,
        // altrimenti il valore corrispondente a quella chiave
        int occorrenzePrecedenti = mioHashMap.get(element) == null ? 0 : mioHashMap.get(element);
        mioHashMap.put((E) element, count);

        // Per modificare in modo corretto il numero di elementi
        // utilizzo occorrenzePrecedenti per sapere se dovrò sommare o sottrarre il valore di count
        if(occorrenzePrecedenti == 0 || count > occorrenzePrecedenti)
            elementiPresenti += count-occorrenzePrecedenti;
        else if (occorrenzePrecedenti > count)
            elementiPresenti -= occorrenzePrecedenti - count;

        // Incremento le modifiche
        if(occorrenzePrecedenti != count)
            modificheEffettuate++;
        return occorrenzePrecedenti;
    }

    /**
     * Restituisce l'insieme di elementi distinti contenuti in questo
     * multinsieme. L'ordine degli elementi nel set risultato non è specificato.
     *
     * @return l'insieme di elementi distinti in questo multinsieme
     */
    @Override
    public Set<E> elementSet() {
        return new HashSet<E>(mioHashMap.keySet());
    }
    /**
     * Restituisce un iteratore per questo multinsieme. L'iteratore deve
     * presentare tutti gli elementi del multinsieme (in un ordine qualsiasi) e
     * per ogni elemento deve presentare tutte le occorrenze. Le occorrenze
     * dello stesso elemento devono essere presentate in sequenza. L'iteratore
     * restituito non implementa l'operazione {@code remove()}.
     *
     * L'iteratore restituito deve essere <b>fail-fast</b>: se il multinsieme
     * viene modificato strutturalmente (cioè viene fatta un'aggiunta o una
     * cancellazione di almeno un'occorrenza) in qualsiasi momento dopo la
     * creazione dell'iteratore, l'iteratore dovrà lanciare una
     * {@code ConcurrentModificationException} alla chiamata successiva del
     * metodo {@code next()}.
     *
     * @return un iteratore per questo multinsieme
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Determina se questo multinsieme contiene l'elemento specificato.
     *
     * @param element
     *                    l'elemento da cercare
     * @return {@code true} se questo multinsieme contiene almeno una occorrenza
     *         di un elemento {@code e} tale che
     *         {@code element.equals(e) == true}
     * @throws NullPointerException
     *                                  se {@code element} è null
     */
    @Override
    public boolean contains(Object element) {
        if(element == null)
            throw new NullPointerException("Il MyMultiset non può contenere oggetti null");
        return mioHashMap.containsKey(element);
    }

    /**
     * Rimuove tutti gli elementi da questo multinsieme. Il multinsieme sarà
     * vuoto dopo il ritorno da questo metodo.
     */
    @Override
    public void clear() {
        mioHashMap.clear();
        // Azzero il numero di elementi
        elementiPresenti = 0;
        modificheEffettuate++;
    }

    /**
     * Determina se questo multinsieme è vuoto.
     *
     * @return {@code true} se questo multinsieme è vuoto, {@code false} se
     *         contiene almeno una occorrenza di un elemento
     */
    @Override
    public boolean isEmpty() {
        return mioHashMap.isEmpty();
    }

    /**
     * Due multinsiemi sono uguali se e solo se contengono esattamente gli
     * stessi elementi (utilizzando l'equals della classe E) con le stesse
     * molteplicità.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            throw new NullPointerException("");

        if (!(obj instanceof MyMultiset)) return false;

        MyMultiset<E> temp = (MyMultiset<E>) obj;

        // Se non hanno lo stesso numero di elementi, sicuramente non sono uguali
        if (temp.size() != this.size())
            return false;

       // Per sapere se tutti gli elementi di thi.mioHashMap sono presenti e hanno
        // lo stesso numero di occorrenze temp.mioHashMap, ovvero dell'oggetto passato come parametro.
        // Utilizzo un foreach in quanto non devo modificare i valori, ma solo confrontarli.
        for (E e : this.mioHashMap.keySet()) {
            Integer nullIsFalse = temp.mioHashMap.get(e);
            // Se f è null vuol dire che non si hanno occorrenze di quel elemento,
            // di conseguenza non è presente
            if (f == null || !f.equals(this.mioHashMap.get(e)))
                return false;
        }
        return true;
    }

    /**
     * Da ridefinire in accordo con la ridefinizione di equals.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;

        for (E e : mioHashMap.keySet())
            hash += 31 * e.hashCode() + mioHashMap.get(e).hashCode();

        return hash;
    }
}
