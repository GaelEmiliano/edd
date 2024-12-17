package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        private Iterador() {
            cola = new Cola<>();
            if (raiz != null)
                cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            Vertice vertice = cola.saca();
            if (vertice.izquierdo != null)
                cola.mete(vertice.izquierdo);
            if (vertice.derecho != null)
                cola.mete(vertice.derecho);
            return vertice.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Convierte un número entero a su representación binaria como una
     * cadena de caracteres.
     * @param numero El número entero que se desea convertir a binario.
     * @return Una cadena de caracteres que representa la representación
     *         binaria del número.
     */
    private String intToBinary(int numero) {
        StringBuilder sb = new StringBuilder();
        while (numero > 0) {
            /* Agrega el dígito menos significativo del
               número a la cadena binaria */
            sb.append((numero & 1) == 1 ? '1' : '0');
            /* Desplaza el número un bit a la derecha
               para obtener el siguiente dígito */
            numero >>= 1;
        }
        return sb.toString();
    }
    
    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
	    throw new IllegalArgumentException();
        Vertice nuevo = nuevoVertice(elemento);
        elementos++;
        if (raiz == null) {
            raiz = nuevo;
            return;
        }
        /* Implementación con complejidad
           en tiempo O(log n) */
        Vertice actual = raiz;
        String ruta = intToBinary(elementos); // Ruta binaria
        int i = ruta.length() - 2; // Penúltimo dígito
        /* Recorremos el árbol según la ruta binaria para
           encontrar la ubicación del nuevo vértice */
        while (i > 0) {
            if (ruta.charAt(i) == '0')
                actual = actual.izquierdo;
            else
                actual = actual.derecho;
            i--;
        }
        /* Agregamos el nuevo vértice como hijo
           izquierdo o derecho */
        if (ruta.charAt(0) == '0') {
            actual.izquierdo = nuevo;
            nuevo.padre = actual; 
        } else {
            actual.derecho = nuevo;
            nuevo.padre = actual;
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = vertice(busca(elemento));
        if (v == null)
            return;
        elementos--;
        if (elementos == 0) {
            raiz = null;
            return;
        }
        /* Realizamos un recorrido BFS para encontrar
           el último vértice del árbol */
        Cola<Vertice> q = new Cola<>();
        q.mete(raiz);
        Vertice ultimo = null;
        while (!q.esVacia()) {
            ultimo = q.saca();
            if (ultimo.izquierdo != null)
                q.mete(ultimo.izquierdo);
            if (ultimo.derecho != null)
                q.mete(ultimo.derecho);
        }
        /* Intercambiamos elementos */
        T e = v.elemento;
        v.elemento = ultimo.elemento;
        ultimo.elemento = e;
        /* Eliminamos el último vértice. Sea este el
           izquierdo o el derecho. */
        if (ultimo.padre.izquierdo == ultimo)
            ultimo.padre.izquierdo = null;
        else
            ultimo.padre.derecho = null;
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        if (raiz == null)
            return -1;
        return (int)Math.floor(Math.log(elementos) / Math.log(2));
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null)
            return;
        Cola<Vertice> q = new Cola<>();
        q.mete(raiz);
        while (!q.esVacia()) {
            Vertice v = q.saca();
            accion.actua(v);
            if (v.izquierdo != null)
                q.mete(v.izquierdo);
            if (v.derecho != null)
                q.mete(v.derecho);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
