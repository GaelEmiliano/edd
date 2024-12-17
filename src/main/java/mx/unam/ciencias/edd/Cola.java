package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
        if (cabeza == null)
            return "";
        StringBuilder sb = new StringBuilder();
        Nodo aux = cabeza;
        while (aux != null) {
            sb.append(aux.elemento).append(",");
            aux = aux.siguiente;
        }
        return sb.toString();
    }

    /**
     * Agrega un elemento al final de la cola.
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
        else
            rabo.siguiente = rabo = nodo;
    }
}
