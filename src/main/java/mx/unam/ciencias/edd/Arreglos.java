package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Intercambia dos elementos en un arreglo.
     * @param <T> tipo de los elementos en el arreglo.
     * @param arreglo el arreglo en el que se van a intercambiar los elementos.
     * @param i el índice del primer elemento a intercambiar.
     * @param j el índice del segundo elemento a intercambiar.
     */
    private static <T> void intercambia(T[] arreglo, int i, int j) {	
        T e = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = e;
    }

    /**
     * Ordena un subarreglo del arreglo dado usando el algoritmo QuickSort de manera
     * <em>recursiva</em>. El método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos del arreglo.
     * @param <T> tipo de los elementos en el arreglo.
     * @param arreglo el arreglo que se va a ordenar.
     * @param comparador el comparador que define el orden de los elementos.
     * @param a el índice del primer elemento del subarreglo a ordenar.
     * @param b el índice del último elemento del subarreglo a ordenar.
     */
    private static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador, int a, int b) {
        if (b <= a)
            return;
        int i = a + 1;
        int j = b;
        /* Proceso de particionado. */
        while (i < j) {
            if (comparador.compare(arreglo[i], arreglo[a]) > 0 &&
                comparador.compare(arreglo[j], arreglo[a]) <= 0)
                intercambia(arreglo, i++, j--);
            else if (comparador.compare(arreglo[i], arreglo[a]) <= 0)
                i++;
            else
                /* Tiene que ocurrir que A[j] > A[a] */
                j--;
        }
        /* Colocamos el pivote en su posición final. */
        if (comparador.compare(arreglo[i], arreglo[a]) > 0)
            i--;
        intercambia(arreglo, a, i);
        /* Llamadas recursivas a quickSort para ordenar los
           subarreglos izquierdo y derecho del pivote. */
        quickSort(arreglo, comparador, a, i - 1);
        quickSort(arreglo, comparador, i + 1, b);
    }
    
    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador) {
        quickSort(arreglo, comparador, 0, arreglo.length - 1);
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador) {
        for (int i = 0; i < arreglo.length - 1; i++) {
            int m = i;
            /* Buscamos el índice del elemento mínimo en
               el subarreglo restante */
            for (int j = i + 1; j < arreglo.length; j++) {
                if (comparador.compare(arreglo[j], arreglo[m]) < 0)
                    m = j;
            }
            intercambia(arreglo, i, m);
        }
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Realiza una búsqueda binaria <em>recursiva</em> del elemento en el arreglo.
     * Devuelve el índice del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo en el que buscar.
     * @param e el elemento que se busca.
     * @param comparador el comparador utilizado para la búsqueda.
     * @param a el índice inicial del rango de búsqueda.
     * @param b el índice final del rango de búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    private static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador,
                    int a, int b) {
        if (b < a)
            return -1; // Caso base: el rango es inválido
        int m = a + (b - a) / 2; // Punto medio del rango de búsqueda
        int comparacion = comparador.compare(arreglo[m], elemento);
        if (comparacion == 0)
            return m; // El elemento medio es el elemento buscado
        if (comparacion < 0)
            /* Buscar en la mitad superior. */
            return busquedaBinaria(arreglo, elemento, comparador, m + 1, b);
        /* Buscar en la mitad inferior. */
        return busquedaBinaria(arreglo, elemento, comparador, a, m - 1);
    }
    
    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
        return busquedaBinaria(arreglo, elemento, comparador, 0, arreglo.length - 1);
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }
}
