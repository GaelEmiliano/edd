package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            if (color == Color.ROJO) {
                sb.append("R{").append(elemento).append("}");
                return sb.toString();
            }
            sb.append("N{").append(elemento).append("}");
            return sb.toString();
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            return (color == vertice.color && super.equals(objeto));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }
    
    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        if (vertice.getClass() != VerticeRojinegro.class)
            throw new ClassCastException();
        VerticeRojinegro v = (VerticeRojinegro)vertice;
        return v.color;
    }

    /**
     * Rebalancea un Árbol Rojinegro al agregar.
     */
    private void rebalanceaDeAgrega(VerticeRojinegro vertice) {
        /* El vértice es la raíz. */
        if (vertice.padre == null) {
            vertice.color = Color.NEGRO;
            return;
        }
        /* El padre del vértice es negro. */
        VerticeRojinegro p = (VerticeRojinegro)vertice.padre;
        if (p.color == Color.NEGRO)
            return;
        /* El tio del vértice es rojo */
        VerticeRojinegro a = (VerticeRojinegro)p.padre;
        VerticeRojinegro t;
        if (p.esHijoDerecho())
            t = (VerticeRojinegro)a.izquierdo;
        else
            t = (VerticeRojinegro)a.derecho;
        if (t != null && t.color == Color.ROJO) {
            t.color = Color.NEGRO;
            p.color = Color.NEGRO;
            a.color = Color.ROJO;
            rebalanceaDeAgrega(a);
            return;
        }
        /* Cruzado con su padre. */
        VerticeRojinegro aux;
        if (p.esHijoDerecho() != vertice.esHijoDerecho()) {
            if (p.esHijoIzquierdo())
                super.giraIzquierda(p);
            else
                super.giraDerecha(p);
            aux = p;
            p = vertice;
            vertice = aux;
        }
        /* No está cruzado con su padre.*/
        p.color = Color.NEGRO;
        a.color = Color.ROJO;
        if (vertice.esHijoIzquierdo())
            super.giraDerecha(a);
        else
            super.giraIzquierda(a);
    }
    
    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro v = (VerticeRojinegro)ultimoAgregado;
        v.color = Color.ROJO;
        rebalanceaDeAgrega(v);
    }

    /**
     * Verifica si un vértice es rojo bajo la definición de Árbol Rojinegro.
     * @param vertice el vértice que se quiere ver si es rojo. Si lo es regresa
     * @return Si es rojo <code>true</code>, en caso contrario regresa <code>false</code>.
     */
    private boolean esRojo(VerticeRojinegro vertice) {
        return (vertice != null && vertice.color == Color.ROJO);
    }

    /**
     * Verifica si un vértice es negro bajo la definición de Árbol Rojinegro.
     * @param vertice el vértice que se quiere ver si es negro.
     * @return Si es negro <code>true</code>, en caso contrario regresa <code>false</code>.
     */
    private boolean esNegro(VerticeRojinegro vertice) {
        return (vertice == null || vertice.color == Color.NEGRO);
    }

    /**
     * Obtiene el hermano del vértice. Si el vértice es hijo izquierdo, el hermano
     * es el vértice hijo derecho del padre del vértice. Si es derecho el vértice,
     * el hermano es el hijo izquierdo del padre del vértice.
     * @param vertice el vértice del que se quiere obtener su hermano.
     * @return el hermano del vértice.
     */
    private VerticeRojinegro getHermano(VerticeRojinegro vertice) {
        if (vertice.esHijoIzquierdo())
            return (VerticeRojinegro)vertice.padre.derecho;
        return (VerticeRojinegro)vertice.padre.izquierdo;
    }

    /**
     * Rebalancea un Árbol Rojinegro al eliminar.
     */
    private void rebalanceaDeElimina(VerticeRojinegro vertice) {
        /* Declaramos vértices rojinegros */
        VerticeRojinegro p, h, hi, hd;
        if (vertice.padre == null) // Caso 1
            return; // vertice es negro, por tanto no se cambia de color
        p = (VerticeRojinegro) vertice.padre;
        h = getHermano(vertice); // hermano
        // Caso 2
        if (esRojo(h)) {
            p.color = Color.ROJO;
            h.color = Color.NEGRO;
            if (vertice.esHijoIzquierdo())
                super.giraIzquierda(p);
            else
                super.giraDerecha(p);
            p = (VerticeRojinegro)vertice.padre;
            h = getHermano(vertice);
        }
        hi = (VerticeRojinegro)h.izquierdo;
        hd = (VerticeRojinegro)h.derecho;
        // Caso 3
        if (esNegro(p) && esNegro(h) && esNegro(hi) && esNegro(hd)) {
            h.color = Color.ROJO;
            rebalanceaDeElimina(p);
            return;
        }
        // Caso 4
        if (esNegro(h) && esNegro(hi) && esNegro(hd)) {
            h.color = Color.ROJO;
            p.color = Color.NEGRO;
            return;
        }
        // Caso 5
        if (vertice.esHijoIzquierdo() && esRojo(hi) && esNegro(hd) ||
            vertice.esHijoDerecho() && esNegro(hi) && esRojo(hd)) {
            h.color = Color.ROJO;
            if (esRojo(hd))
                hd.color = Color.NEGRO;
            else
                hi.color = Color.NEGRO;
            if (vertice.esHijoDerecho())
                super.giraIzquierda(h);
            else
                super.giraDerecha(h);
            h = getHermano(vertice);
            hi = (VerticeRojinegro)h.izquierdo;
            hd = (VerticeRojinegro)h.derecho;
        }
        // Caso 6
        h.color = p.color;
        p.color = Color.NEGRO;
        if (vertice.esHijoIzquierdo()) {
            hd.color = Color.NEGRO;
            super.giraIzquierda(p);
        } else {
            hi.color = Color.NEGRO;
            super.giraDerecha(p);
        }
     }

    /**
     * Elimina un vértice fantasma, es decir, aquel que no tenga ningun elemento.
     * Si su elemento es <code>null</code> lo desconecta del resto del árbol.
     * @param vertice el vértice fantasma a eliminar.
     */
    private void eliminaFantasma(VerticeRojinegro vertice) {
        if (vertice.elemento == null)
            eliminaVertice(vertice);
    }
    
    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro v = (VerticeRojinegro)busca(elemento);
        if (v == null)
            return;
        elementos--;
        if (v.izquierdo != null)
            v = (VerticeRojinegro) intercambiaEliminable(v);
        if (v.derecho == null && v.izquierdo == null) {
            VerticeRojinegro fantasma = (VerticeRojinegro)nuevoVertice(null);
            fantasma.color = Color.NEGRO;
            v.izquierdo = fantasma;
            fantasma.padre = v;
        }
        VerticeRojinegro h = (VerticeRojinegro)(v.izquierdo != null ? v.izquierdo : v.derecho);
        eliminaVertice(v);
        if (esRojo(h)) {
            h.color = Color.NEGRO;
            eliminaFantasma(h); // h puede ser fantasma, o no
            return;
        }
        if (esRojo(v)) {
            eliminaFantasma(h); // h puede ser fantasma, o no
            return;
        }
        rebalanceaDeElimina(h);
        eliminaFantasma(h); // h puede ser fantasma, o no
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
