package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            pila = new Pila<>(); 
            Vertice v = raiz;
            /* Agregamos toda la primer
               rama izquierda del árbol. */
            while (v != null) {
                pila.mete(v);
                v = v.izquierdo;
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {            
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            Vertice v = pila.saca();
            if (v.derecho != null) {
                Vertice actual = v.derecho;
                /* Se mete toda la rama
                   izquierda. */
                while (actual != null) {
                    pila.mete(actual);
                    actual = actual.izquierdo;
                }
            }
            return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega <em>recursivamente</em> un elemento al árbol binario, comenzando
     * desde el vértice actual. Si el elemento es menor o igual al elemento del
     * vértice actual, se agrega en el subárbol izquierdo.
     * Si es mayor, se agrega en el subárbol derecho.
     * @param actual el vértice actual en el que se está considerando agregar el
     *        elemento.
     * @param elemento el elemento a agregar en el árbol.
     */
    private void agrega(Vertice actual, Vertice nuevo) {
        if (nuevo.elemento.compareTo(actual.elemento) <= 0) {
            if (actual.izquierdo == null) {
                actual.izquierdo = nuevo;
                actual.izquierdo.padre = actual;
                ultimoAgregado = actual.izquierdo;
            } else
                agrega(actual.izquierdo, nuevo);
        } else {
            if (actual.derecho == null) {
                actual.derecho = nuevo;
                actual.derecho.padre = actual;
                ultimoAgregado = actual.derecho;
            } else
                agrega(actual.derecho, nuevo);
        }
    }
    
    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Vertice nuevo = nuevoVertice(elemento);
        elementos++;
        ultimoAgregado = nuevo;
        if (raiz == null) {
            raiz = nuevo;
            ultimoAgregado = raiz;
            return;
        }
        agrega(raiz, nuevo);
    }

    /**
     * Encuentra el vértice máximo en el subárbol.
     * @param vertice el vértice desde el que se inicia la búsqueda.
     * @return el vértice máximo en el subárbol.
     */
    protected Vertice maximoEnSubarbol(Vertice vertice) {
        if (vertice.derecho == null)
            return vertice;
        return maximoEnSubarbol(vertice.derecho);
    }
    
    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = vertice(busca(elemento));
        if (v == null)
            return;
        if (elementos == 1) {
            limpia();
        } else {
            elementos--;
            /* El vértice a eliminar es hoja */
            if (v.izquierdo == null && v.derecho == null) {
                if (v.padre.izquierdo == v)
                    v.padre.izquierdo = null;
                else
                    v.padre.derecho = null;
                return;
            }
            /* El vértice a eliminar tiene a lo más un hijo */
            if (v.izquierdo != null && v.derecho == null ||
                v.izquierdo == null && v.derecho != null) {
                eliminaVertice(v);
                return;
            }
            /* El vértice a eliminar tiene dos hijos
               distintos de null */
            Vertice u = intercambiaEliminable(v);
            eliminaVertice(u);
        }
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {       
        Vertice maximoIzquierdo = maximoEnSubarbol(vertice.izquierdo);
        /* Intercambiamos los elementos */
        T e = vertice.elemento;
        vertice.elemento = maximoIzquierdo.elemento;
        maximoIzquierdo.elemento = e;
        /* Lo regresamos para que sea eliminado. */
        return maximoIzquierdo;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        Vertice u = (vertice.izquierdo != null)
            ? vertice.izquierdo
            : vertice.derecho;
        Vertice p = vertice.padre;
        if (p != null) {
            if (p.izquierdo == vertice)
                p.izquierdo = u;
            else
                p.derecho = u;
        } else
            raiz = u;
        if (u != null)
            u.padre = vertice.padre;
    }

    /**
     * Busca <em>recursivamente</em> un elemento en el árbol binario
     * ordenado a partir del vértice dado.
     * @param vertice el vértice actual que se está considerando.
     * @param elemento el elemento que se está buscando en el árbol.
     * @return el vértice que contiene el elemento buscado,
     *         o <code>null</code> si no se encuentra.
     */
    private VerticeArbolBinario<T> busca(Vertice vertice, T elemento) {
        if (vertice == null)
            return null;
        if (vertice.elemento.equals(elemento))
            return vertice;
        if (elemento.compareTo(vertice.elemento) < 0)
            return busca(vertice.izquierdo, elemento);
        return busca(vertice.derecho, elemento);
    }
    
    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz, elemento);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);
        Vertice p = v.izquierdo; // Hijo izquierdo
        if (v == null || v.izquierdo == null)
            return;        
        v.izquierdo = p.derecho;
        if (p.derecho != null)
            p.derecho.padre = v;
        p.padre = v.padre;
        if (v.padre == null)
            raiz = p;
        else if (v == v.padre.izquierdo)
            v.padre.izquierdo = p;
        else
            v.padre.derecho = p;
        p.derecho = v;
        v.padre = p;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);
        Vertice q = v.derecho; // Hijo derecho
        if (v == null || v.derecho == null)
            return;
        v.derecho = q.izquierdo;
        if (q.izquierdo != null)
            q.izquierdo.padre = v;
        q.padre = v.padre;
        if (v.padre == null)
            raiz = q;
        else if (v == v.padre.izquierdo)
            v.padre.izquierdo = q;
        else
            v.padre.derecho = q;
        q.izquierdo = v;
        v.padre = q;
    }

    /**
     * Realiza un recorrido en DFS <em>pre-order</em> de un árbol binario,
     * comenzando desde el vértice dado, y realiza una acción sobre cada vértice.
     * @param vertice el vértice desde el cual comenzar el recorrido en preorden.
     * @param accion La acción a realizar sobre cada vértice.
     */
    private void
    dfsPreOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        accion.actua(vertice);
        dfsPreOrder(vertice.izquierdo, accion);
        dfsPreOrder(vertice.derecho, accion);
    }
    
    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(raiz, accion);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param vertice el vértice desde el cual comenzar el recorrido en orden.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void
    dfsInOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        dfsInOrder(vertice.izquierdo, accion);
        accion.actua(vertice);
        dfsInOrder(vertice.derecho, accion);
    }
    
    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(raiz, accion);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param vertice el vértice recibido desde el cual comenzar el recorrido
     *        en postorden.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void
    dfsPostOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        dfsPostOrder(vertice.izquierdo, accion);
        dfsPostOrder(vertice.derecho, accion);
        accion.actua(vertice);
    }
    
    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(raiz, accion);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
