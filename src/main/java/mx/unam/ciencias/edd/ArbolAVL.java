package mx.unam.ciencias.edd;

/**
 * <p>Clase para árboles AVL.</p>
 *
 * <p>Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre
 * la áltura de sus subárboles izquierdo y derecho está entre -1 y 1.</p>
 */
public class ArbolAVL<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeAVL extends Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
            super(elemento);
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
            return altura;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * @return una representación en cadena del vértice AVL.
         */
        @Override public String toString() {
            return elemento.toString() + " " + altura + "/" + balancea(this);
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente
         *         iguales, y las alturas son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked") VerticeAVL vertice = (VerticeAVL)objeto;
            return (altura == vertice.altura && super.equals(objeto));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolAVL() { super(); }

    /**
     * Construye un árbol AVL a partir de una colección. El árbol AVL tiene los
     * mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol AVL.
     */
    public ArbolAVL(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }

    /**
     * Nos da la altura de un verticeAVL. La diferencia con {@link altura} es
     * que si el vértice es <code>null</code> la altura será de -1,
     * en otro caso se regresa la altura del vértice.
     * @param vertice el vértice del que obtendremos su altura.
     * @return la altura del vértice, o -1 si el vértice es <code>null</code>.
     */
    private int alturaVertice(VerticeAVL vertice) {
        return (vertice == null) ? -1 : vertice.altura;
    }

    /**
     * Nos da el balance de un vértice. Definido como la diferencia de la altura
     * de su subárbol izquierdo menos la altura de su subárbol derecho.
     * @param vertice el vértice del cual obtendremos su balance.
     * @return el balance del vértice.
     */
    private int balancea(VerticeAVL vertice) {
        VerticeAVL verticeIzquierdo = (VerticeAVL)vertice.izquierdo;
        VerticeAVL verticeDerecho = (VerticeAVL)vertice.derecho;
        return alturaVertice(verticeIzquierdo) - alturaVertice(verticeDerecho);
    }

    /**
     * Modifica la altura de un verticeAVL.
     * Utiliza el método auxiliar {@link alturaVertice} para obtener
     * la altura de los subárboles izquierdo y derecho.
     * @param vertice el vértice al que le modificaremos su altura.
     */
    private void modificaAlturaVertice(VerticeAVL vertice) {
        VerticeAVL verticeIzquierdo = (VerticeAVL)vertice.izquierdo;
        VerticeAVL verticeDerecho = (VerticeAVL)vertice.derecho;
        vertice.altura = Math.max(alturaVertice(verticeIzquierdo),
                                  alturaVertice(verticeDerecho)) + 1;
    }

    /**
     * Rebalancea un árbol AVL después de agregar o eliminar un verticeAVL.
     * Hace que todos los vértices del árbol tengan un balance entre -1 y 1.
     * Al girar sobre un vértice, esto únicamente afectará a los hijos del vértice,
     * y sus antecesores, dejando al resto de los vértices en sus subárboles
     * con la misma altura.
     * @param vertice el vértice desde el que queremos rebalancear el árbol.
     */
    private void rebalancea(VerticeAVL vertice) {
        if (vertice == null)
            return;
        modificaAlturaVertice(vertice);
        if (balancea(vertice) == -2) {
            VerticeAVL hijoDerecho = (VerticeAVL)vertice.derecho;
            if (balancea(hijoDerecho) == 1) {
                super.giraDerecha(hijoDerecho);
                modificaAlturaVertice(hijoDerecho);
                modificaAlturaVertice(vertice);
            }
            super.giraIzquierda(vertice);
            modificaAlturaVertice(vertice);
            modificaAlturaVertice((VerticeAVL)vertice.padre);
        } else if (balancea(vertice) == 2) {
            VerticeAVL hijoIzquierdo = (VerticeAVL)vertice.izquierdo;
            if (balancea(hijoIzquierdo) == -1) {
                super.giraIzquierda(hijoIzquierdo);
                modificaAlturaVertice(hijoIzquierdo);
                modificaAlturaVertice(vertice);
            }
            super.giraDerecha(vertice);
            modificaAlturaVertice(vertice);
            modificaAlturaVertice((VerticeAVL)vertice.padre);
        }
        rebalancea((VerticeAVL)vertice.padre);
    }
    
    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo como
     * sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rebalancea((VerticeAVL)ultimoAgregado.padre);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y gira el árbol como sea necesario para rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeAVL v = (VerticeAVL)busca(elemento);
        if (v == null)
            return;
        elementos--;
        if (v.izquierdo != null && v.derecho != null)
            v = (VerticeAVL)intercambiaEliminable(v);
        eliminaVertice(v);
        rebalancea((VerticeAVL)v.padre);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la derecha por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la izquierda por el " +
                                                "usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la izquierda por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la derecha por el " +
                                                "usuario.");
    }
}
