import java.util.*;

/**
 * @author Luca Tesei (template) ** Simone Cisca - simone.cisca@studenti.unicam.it ** (implementazione)
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
     * Questo metodo restituisce il numero di elementi presenti.
     *
     * @return elementiPresenti
     *          il numero di elementi presenti in @{code mioHashMap}
     */
    @Override
    public int size() {
        return elementiPresenti;
    }

    /**
     * Questo metodo restituisce la value di un oggetto se presente in @{code mioHashMap}
     *
     * @param element
     * @return val
     *          val corrisponde al valore corrispondente alla chiave di @{code element}
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
     * Questo metodo inserisce o aggiunge un nuovo oggetto a @{code mioHashMap}
     * @param element
     * @param occurrences
     * @return occorrenzePrecedenti
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

    // Richiamo il primo metodo passando come parametro element ed 1
    // in quanto si vuole inserire una singola occorrenza
    @Override
    public void add(E element) {
        add(element, 1);
    }

    /**
     * Questo metodo rimuove definitivamente o @{code occurrences} volte @{code element} da @{code mioHashMap}
     * @param element
     * @param occurrences
     * @return occorrenzePrecedenti
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
     * Questo metodo rimuove definitivamente @{code element} da @{code mioHashMap}
     * @param element
     * @return true se
     */
    @Override
    public boolean remove(Object element) {
        return remove(element, 1) > 0;
    }

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

    // Restituisco un set delle sole chiavi in quanto un Set non può contenere valori duplicati
    @Override
    public Set<E> elementSet() {
        return new HashSet<E>(mioHashMap.keySet());
    }

    // Richiamo il costruttore della classe interna Itr e ne restituisco l'oggetto
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    // Utilizzo il metodo containsKey della HashMap
    @Override
    public boolean contains(Object element) {
        if(element == null)
            throw new NullPointerException("Il MyMultiset non può contenere oggetti null");
        return mioHashMap.containsKey(element);
    }

    // La pulizia viene eseguita con il metodo clear della HashMap
    @Override
    public void clear() {
        mioHashMap.clear();
        // Azzero il numero di elementi
        elementiPresenti = 0;
        modificheEffettuate++;
    }

    // Se la HashMap è vuota, lo sarà anche MyMultiset
    @Override
    public boolean isEmpty() {
        return mioHashMap.isEmpty();
    }

    /*
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

        // Se non hanno lo stesso numero di elementi, sono sicuro che non sono uguali
        if (temp.size() != this.size())
            return false;

        // Per sapere se tutti gli elementi di thi.mioHashMap sono presenti e hanno
        // lo stesso numero di occorrenze temp.mioHashMap, ovvero dell'oggetto passato come parametro.
        // Utilizzo un foreach in quanto non devo modificare i valori, ma solo confrontarli.
        for (E e : this.mioHashMap.keySet()) {
            Integer f = temp.mioHashMap.get(e);
            // Se f è null vuol dire che non si hanno occorrenze di quel elemento, di conseguenza non è presente
            if (f == null || !f.equals(this.mioHashMap.get(e)))
                return false;
        }

        return true;
    }

    /*
     * Da ridefinire in accordo con la ridefinizione di equals.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;

        for (E e : mioHashMap.keySet())
            hash += 2 * e.hashCode() + mioHashMap.get(e).hashCode();

        return hash;
    }
}
