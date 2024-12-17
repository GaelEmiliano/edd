package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        if (cabeza == null)
            return "";
        StringBuilder sb = new StringBuilder();
        Nodo aux = cabeza;
        while (aux != null) {
            sb.append(aux.elemento).append("\n");
            aux = aux.siguiente;
        }
        return sb.toString();
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Nodo nodo = new Nodo(elemento);
        if (cabeza == null)
            cabeza = rabo = nodo;
        else {
            nodo.siguiente = cabeza;
            cabeza = nodo;
        }
    }
}
