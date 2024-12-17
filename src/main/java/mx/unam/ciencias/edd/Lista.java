package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        private T elemento;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nodo con un elemento. */
        private Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private Iterador() {
            start();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            if (siguiente == null)
                throw new NoSuchElementException();
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            if (anterior == null)
                throw new NoSuchElementException();
            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            anterior = null;
            siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            siguiente = null;
            anterior = rabo;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return getLongitud();
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return rabo == null;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Nodo nodo = new Nodo(elemento);
        longitud++;
        if (rabo == null)
            cabeza = rabo = nodo;
        else {
            rabo.siguiente = nodo;
            nodo.anterior = rabo;
            rabo = nodo;
        }
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Nodo nodo = new Nodo(elemento);
        longitud++;
        if (rabo == null)
            cabeza = rabo = nodo;
        else {
            cabeza.anterior = nodo;
            nodo.siguiente = cabeza;
            cabeza = nodo;
        }
    }

    /**
     * Retorna el nodo en la posición especificada
     * en la lista. 
     * El primer nodo se encuentra en la posición 0.
     * @param i la posición del nodo que se desea obtener.
     * @return el nodo en la posición especificada.
     */
    private Nodo getNodo(int i) {
        Nodo nodoAux = cabeza;
        int contador = 0;
        while (contador != i) {
            nodoAux = nodoAux.siguiente;
            contador++;
        }
        return nodoAux;
    }
    
    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        if (i <= 0)
            agregaInicio(elemento);
        else if (i >= longitud)
            agregaFinal(elemento);
        else {
            longitud++;
            Nodo nodo = new Nodo(elemento);
            Nodo sig = getNodo(i);
            Nodo pre = sig.anterior;
            nodo.anterior = pre;
            pre.siguiente = nodo;
            nodo.siguiente = sig;
            sig.anterior = nodo;
        }
    }

    /**
     * Busca un nodo que contenga el elemento especificado en la lista
     * doblemente ligada.     
     * @param elemento el elemento que se desea buscar en la lista.
     * @return el nodo que contiene el elemento especificado,
     *         o <code>null</code> si no se encuentra.
     */
    private Nodo buscaNodo(T elemento) {
        Nodo nodoAux = cabeza;
        while (nodoAux != null) {
            if (nodoAux.elemento.equals(elemento))
                return nodoAux;
            nodoAux = nodoAux.siguiente;
        }
        return null;
    }

    /**
     * Elimina un nodo específico de la lista doblemente ligada.
     * Se ajustan las referencias de los nodos adyacentes para mantener
     * la integridad de la lista.
     * Si el nodo a eliminar es el único nodo en la lista, la cabeza
     * y el rabo se establecen en <code>null</code>.     
     * @param nodo el nodo que se desea eliminar de la lista.
     */
    private void eliminaNodo(Nodo nodo) {
        longitud--;
        if (cabeza == rabo)
            cabeza = rabo = null;
        else if (nodo == cabeza) {
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
        } else if (nodo == rabo) {
            rabo = rabo.anterior;
            rabo.siguiente = null;
        } else {
            nodo.siguiente.anterior =
                nodo.anterior;
            nodo.anterior.siguiente =
                nodo.siguiente;
        }	
    }
    
    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Nodo nodoBuscado = buscaNodo(elemento);
        if (nodoBuscado == null)
            return;
        eliminaNodo(nodoBuscado);
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if (cabeza == null)
            throw new NoSuchElementException();
        T elemento = cabeza.elemento;
        eliminaNodo(cabeza);
        return elemento;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if (rabo == null)
            throw new NoSuchElementException();
        T elemento = rabo.elemento;
        eliminaNodo(rabo);
        return elemento;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return buscaNodo(elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> lista = new Lista<>();
        Nodo nodo = rabo;
        while (nodo != null) {
            lista.agregaFinal(nodo.elemento);
            nodo = nodo.anterior;
        }
        return lista;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Lista<T> lista = new Lista<>();
        Nodo nodo = cabeza;
        while (nodo != null) {
            lista.agregaFinal(nodo.elemento);
            nodo = nodo.siguiente;
        }
        return lista;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if (rabo == null)
            throw new NoSuchElementException();
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if (rabo == null)
            throw new NoSuchElementException();
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if (i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido();
        return getNodo(i).elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        int i = 0;
        Nodo nodoAux = cabeza;
        while (nodoAux != null) {
            if (nodoAux.elemento.equals(elemento))
                return i;
            i++;
            nodoAux = nodoAux.siguiente;
        }
        return -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        if (rabo == null)
            return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(cabeza.elemento);
        Nodo nodoAux = cabeza.siguiente;
        while (nodoAux != null) {
            sb.append(", ")
              .append(nodoAux.elemento);
            nodoAux = nodoAux.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
        if (longitud != lista.longitud)
            return false;
        Nodo nodoAux1 = cabeza;
        Nodo nodoAux2 = lista.cabeza;
        while (nodoAux1 != null) {
            if (!nodoAux1.elemento.equals(nodoAux2.elemento))
                return false;
            nodoAux1 = nodoAux1.siguiente;
            nodoAux2 = nodoAux2.siguiente;
        }
        return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Combina y mezcla dos listas ordenadas en una sola lista ordenada.
     * El método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param <T> el tipo de elementos en las listas.
     * @param lista1 la primera lista ordenada a mezclar.
     * @param lista2 la segunda lista ordenada a mezclar.
     * @param comparador el comparador utilizado para determinar el
     *        orden de los elementos.
     * @return una nueva lista que contiene los elementos de las listas
     *         dadas, mezclados y ordenados.
     */
    private Lista<T>
    mezcla(Lista<T> lista1, Lista<T> lista2, Comparator<T> comparador) {
        Lista<T> ordenada = new Lista<>();
        Nodo nodo1 = lista1.cabeza;
        Nodo nodo2 = lista2.cabeza;
        /* Mezclamos ordenadamente los elementos de las listas */
        while (nodo1 != null && nodo2 != null) {
            if (comparador.compare(nodo1.elemento, nodo2.elemento) <= 0) {
                ordenada.agregaFinal(nodo1.elemento);
                nodo1 = nodo1.siguiente;
            } else {
                ordenada.agregaFinal(nodo2.elemento);
                nodo2 = nodo2.siguiente;
            }
        }
        /* Agregamos los elementos restantes de lista1 (si hay) */
        while (nodo1 != null) {
            ordenada.agregaFinal(nodo1.elemento);
            nodo1 = nodo1.siguiente;
        }
        /* Agregamos los elementos restantes de lista2 (si hay) */
        while (nodo2 != null) {
            ordenada.agregaFinal(nodo2.elemento);
            nodo2 = nodo2.siguiente;
        }
        return ordenada;
    }

    /**
     * Ordena una lista utilizando el algoritmo MergeSort de manera
     * recursiva. El método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param <T> el tipo de elementos en la lista.
     * @param lista la lista que se va a ordenar.
     * @param comparador el comparador utilizado para determinar el orden de
     *        los elementos.
     * @return una nueva lista que contiene los elementos de la lista dada, ordenados.
     */
    private Lista<T> mergeSort(Lista<T> lista, Comparator<T> comparador) {
        if (lista.longitud < 2)
            return lista.copia();
        int mitad = lista.longitud / 2;
        /* Dividimos la lista en dos sublistas. */
        Lista<T> sublista1 = new Lista<>();
        Lista<T> sublista2 = new Lista<>();
        Nodo nodoAux = lista.cabeza;
        for (int i = 0; i < lista.longitud; i++) {
            if (i < mitad)
                sublista1.agregaFinal(nodoAux.elemento);
            else
                sublista2.agregaFinal(nodoAux.elemento);
            nodoAux = nodoAux.siguiente;
        }
        /* Llamadas recursivas para ordenar sublistas. */
        sublista1 = mergeSort(sublista1, comparador);
        sublista2 = mergeSort(sublista2, comparador);
        /* Mezclamos las sublistas ordenadas. */
        return mezcla(sublista1, sublista2, comparador);
    }
    
    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
        return mergeSort(this, comparador);
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        // Aquí va su código.
        Nodo nodoAux = cabeza;
        while (nodoAux != null) {
            if (comparador.compare(nodoAux.elemento, elemento) == 0)
                return true;
            nodoAux = nodoAux.siguiente;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
