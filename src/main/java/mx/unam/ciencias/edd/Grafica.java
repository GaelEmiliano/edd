package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                                     ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        private T elemento;
        /* El color del vértice. */
        private Color color;
        /* La distancia del vértice. */
        private double distancia;
        /* El índice del vértice. */
        private int indice;
        /* La lista de vecinos del vértice. */
        private Lista<Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            vecinos = new Lista<>();	    
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getLongitud();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            return Double.compare(distancia, vertice.distancia);
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.vecinos.getLongitud();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino<T> {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica<T>.Vertice v, Grafica<T>.Vecino a);
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Lista<>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getLongitud();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null || contiene(elemento))
            throw new IllegalArgumentException();
        vertices.agregaFinal(new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if (sonVecinos(a, b) || a == b || peso <= 0)
            throw new IllegalArgumentException();	
        Vertice v1 = (Vertice)vertice(a);
        Vertice v2 = (Vertice)vertice(b);
        v2.vecinos.agregaFinal(new Vecino(v1, peso));
        v1.vecinos.agregaFinal(new Vecino(v2, peso));
        aristas++;
    }
    
    /**
     * Busca y retorna el primer vecino del vértice <code>a</code>
     * que corresponde al vértice <code>b</code>.
     * Este método es útil para determinar si dos vértices en una gráfica
     * están directamente conectados a través de un único salto y, si lo están,
     * obtener la instancia del vecino correspondiente.
     * @param a el vértice desde el cual se busca el vecino. Este vértice debe
     *          existir en la gráfica.
     * @param b el vértice que se busca como vecino de <code>a</code>.
     * @return el <code>Vecino</code> de <code>a</code> que tiene como vértice
     *         a <code>b</code> si existe; <code>null</code> si <code>b<code> no
     *         es vecino de <code>a</code>.
     * @throws IllegalArgumentException si <code>a</code> o <code>b</code> son
     *                                  <code>null</code>.
     */
    private Vecino getVecino(Vertice a, Vertice b) {
        if (a == null || b == null)
            throw new IllegalArgumentException();
        for (Vecino v : a.vecinos)
            if (v.vecino == b)
                return v;
        return null;
    }
    
    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException();
        Vertice v1 = (Vertice)vertice(a);
        Vertice v2 = (Vertice)vertice(b);
        v2.vecinos.elimina(getVecino(v2, v1));
        v1.vecinos.elimina(getVecino(v1, v2));
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice v : vertices)
            if (v.elemento.equals(elemento))
                return true;
        return false;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        if (!contiene(elemento))
            throw new NoSuchElementException();
        Vertice v = (Vertice)vertice(elemento);
        for (Vecino vecino : v.vecinos)
            desconecta(v.elemento, vecino.vecino.elemento);
        vertices.elimina(v);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        Vertice v1 = (Vertice)vertice(a);
        Vertice v2 = (Vertice)vertice(b);
        return getVecino(v1, v2) != null;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException();
        Vertice v1 = (Vertice)vertice(a);
        Vertice v2 = (Vertice)vertice(b);
        return getVecino(v1, v2).peso;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if (!sonVecinos(a, b) || peso <= 0)
            throw new IllegalArgumentException();
        Vertice v1 = (Vertice)vertice(a);
        Vertice v2 = (Vertice)vertice(b);
        getVecino(v1, v2).peso = peso;
        getVecino(v2, v1).peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        for (Vertice v : vertices)
            if (v.elemento.equals(elemento))
                return v;	
        throw new NoSuchElementException();
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null || (vertice.getClass() != Vertice.class
                                && vertice.getClass() != Vecino.class))
            throw new IllegalArgumentException();
        if (vertice.getClass() == Vertice.class) {
            Vertice v = (Vertice)vertice;
            v.color = color;
        }
        if (vertice.getClass() == Vecino.class) {
            Vecino v = (Vecino)vertice;
            v.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        Vertice v = vertices.getPrimero();
        /* Recorremos por amplitud (BFS) y
           coloreamos los vértices de rojo. */
        for (Vertice y : vertices)
            y.color = Color.ROJO;
        v.color = Color.NEGRO;
        Cola<Vertice> q = new Cola<>();
        q.mete(v);
        Vertice u;
        while (!q.esVacia()) {
            u = q.saca();
            for (Vecino vecino : u.vecinos)
                if (vecino.vecino.color == Color.ROJO) {
                    vecino.vecino.color = Color.NEGRO;
                    q.mete(vecino.vecino);
                }
        }
        /* Verificamos si todos son rojos. */
        for (Vertice vertice : vertices)
            if (vertice.color == Color.ROJO)
                return false;
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice v : vertices)
            accion.actua(v);
    }

    /**
     * Recorre la gráfica comenzando desde un vértice dado utilizando un algoritmo de
     * búsqueda, ya sea por amplitud o profundidad; dependiendo de la estructura elegida.
     * @param elemento El elemento que representa el vértice de inicio del recorrido.
     * @param accion La acción a realizar en cada vértice durante el recorrido.
     * @param estructura La estructura de datos (cola o pila) utilizada para el recorrido.
     * @param <T> El tipo de elementos almacenados en los vértices.
     */
    private void recorreGrafica(T elemento, AccionVerticeGrafica<T> accion,
                                MeteSaca<Vertice> estructura) {
        Vertice v = (Vertice)vertice(elemento);
        /* Coloreamos todos los vértices de rojo usando una lambda. */
        paraCadaVertice((vertice) -> setColor(vertice, Color.ROJO));
        v.color = Color.NEGRO;
        estructura.mete(v);
        Vertice u;
        while (!estructura.esVacia()) {
            u = estructura.saca();
            accion.actua(u);
            for (Vecino vecino : u.vecinos)
                if (vecino.vecino.color == Color.ROJO) {
                    vecino.vecino.color = Color.NEGRO;
                    estructura.mete(vecino.vecino);
                }
        }
    }
    
    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        if (!contiene(elemento))
            throw new NoSuchElementException();
        recorreGrafica(elemento, accion, new Cola<Vertice>());
        /* Todos los vértices tendrán color NINGUNO usando lambda. */
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        if (!contiene(elemento))
            throw new NoSuchElementException();
        recorreGrafica(elemento, accion, new Pila<Vertice>());
        /* Todos los vértices tendrán color NINGUNO usando lambda. */
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Vertice vertice : vertices)
            sb.append(String.format("%s, ", vertice.elemento.toString()));
        sb.append("}, {");
        Lista<T> verticesAnteriores = new Lista<>();
        for (Vertice vertice : vertices) {
            for (Vecino vecino : vertice.vecinos)
                if (!verticesAnteriores.contiene(vecino.vecino.elemento))
                    sb.append(String.format("(%s, %s), ",
                                            vertice.elemento.toString(),
                                            vecino.vecino.elemento.toString()));
            verticesAnteriores.agregaFinal(vertice.elemento);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if (getElementos() != grafica.getElementos() || aristas != grafica.aristas)
            return false;
        for (Vertice vertice : vertices) {
            if (!grafica.contiene(vertice.elemento))
                return false;
            for (Vecino vecino : vertice.vecinos)
                if (!grafica.sonVecinos(vertice.elemento, vecino.vecino.elemento))
                    return false;
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Reconstruye la trayectoria desde un vértice destino dado hasta el
     * vértice de origen, según sus distancias y un buscador recibido.
     * Este método auxiliar es el mismo algoritmo para reconstruír la trayectoria
     * de peso mínimo como reconstruír la trayectoria mínima.
     * @param buscador el buscador que nos indica si dos vértices son vecinos.
     * @param destino el vértice desde el cuál reconstruimos la trayectoria.
     * @return la trayectoria como una lista.
     */
    private Lista<VerticeGrafica<T>>
    reconstruyeTrayectoria(BuscadorCamino<T> buscador,
                           Vertice destino) {
        Vertice u = destino;
        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();
        /* Si el vértice de destino tiene distancia infinita indica
           que no hay un camino hacia este vértice desde el origen. */
        if (u.distancia == Double.MAX_VALUE)
            return new Lista<VerticeGrafica<T>>(); // Caso sin solución.
        trayectoria.agregaFinal(u);
        while (u.distancia != 0) {
            /* Iteramos sobre cada vecino de u y verificamos si un
               vértice y su vecino son consecutivos en la trayectoria. */
            for (Vecino vecino : u.vecinos)
                if (buscador.seSiguen(u, vecino)) {
                    trayectoria.agregaFinal(vecino.vecino);
                    u = vecino.vecino;
                    break;
                }	    
        }
        return trayectoria.reversa();
    }
    
    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException();
        Vertice verticeOrigen = (Vertice)vertice(origen);
        if (origen.equals(destino)) {
            /* Si el origen y el detino son el mismo se regresa
               una lista que solo contiene a ese vértice. */
            Lista<VerticeGrafica<T>> lista = new Lista<>();
            lista.agregaFinal(verticeOrigen);
            return lista;
        }
        /* Inicializa la distancia de todos los vértices a
           un valor infinito (muy grande). */
        for (Vertice vertice : vertices)
            vertice.distancia = Double.MAX_VALUE;
        /* Se establece la distacia del vértice de origen en 0
           y se añade a la cola para procesar. Se usa BFS. */
        verticeOrigen.distancia = 0;
        Cola<Vertice> q = new Cola<>();
        q.mete(verticeOrigen);
        /* Procesa cada vértice en la cola hasta que esté vacía. */
        while (!q.esVacia()) {
            verticeOrigen = q.saca();
            /* Revisa todos los vecinos del vértice u. */
            for (Vecino vecino : verticeOrigen.vecinos)
                /* Si el vecino no ha sido visitado (distancia aún en máximo)
                   actualiza su distancia y lo añade a la cola. */
                if (vecino.vecino.distancia == Double.MAX_VALUE) {
                    vecino.vecino.distancia = verticeOrigen.distancia + 1;
                    q.mete(vecino.vecino);
                }
        }
        return reconstruyeTrayectoria
            ((aux, vecino) -> vecino.vecino.distancia == aux.distancia - 1,
             (Vertice)vertice(destino));
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException();
        /* Inicializa la distancia de todos los vértices a
           un valor infinito (muy grande). */
        for (Vertice vertice : vertices)
            vertice.distancia = Double.MAX_VALUE;
        Vertice verticeOrigen = (Vertice)vertice(origen);
        verticeOrigen.distancia = 0;
        /* Usa un mpntículo para optimizar la selección del
           vértice con la menor distancia estimada. */
        MonticuloDijkstra<Vertice> monticulo;
        int n = vertices.getLongitud();
        if (aristas > ((n * (n - 1)) / 2) - n)
            monticulo = new MonticuloArreglo<>(vertices);
        else
            monticulo = new MonticuloMinimo<>(vertices);
        /* Mientras que el montículo no esté vacío, procesa
           cada vértice. */
        while (!monticulo.esVacia()) {
            Vertice raiz = monticulo.elimina();
            /* Revisa cada vecino del vértice eliminado. */
            for (Vecino vecino : raiz.vecinos)
                /* Si la distancia a través de 'raíz' es menor, actualiza la
                   distancia del vecino y reoordena el montículo. */
                if (vecino.vecino.distancia > raiz.distancia + vecino.peso) {
                    vecino.vecino.distancia = raiz.distancia + vecino.peso;
                    monticulo.reordena(vecino.vecino);
                }
        }
        return reconstruyeTrayectoria
            ((vertice, vecino) -> vecino.vecino.distancia + vecino.peso == vertice.distancia,
             (Vertice)vertice(destino));
    }
}
