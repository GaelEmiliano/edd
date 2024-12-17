package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Combina cuatro valores enteros, cada uno representando un byte,
     * en un entero de 32 bits.
     * Utiliza el esquema big-endian para el orden de los bytes en el
     * entero resultante.
     * @param a el primer byte, que se coloca en los 8 bits más
     *          significativos
     * @param b el segundo byte, que se coloca en los siguientes
     *          8 bits.
     * @param c el tercer byte, que se coloca en los terceros 8 bits.
     * @param d el cuarto byte, que se coloca en los 8 bits menos
     *          significativos.
     * @return el entero de 32 bits resultante de la combinación de los
     *         cuatro bytes.
     */
    private static int combina(int a, int b, int c, int d) {
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    /**
     * Extrae un entero de 32 bits desde una posición específica de un
     * arreglo de bytes.
     * La extracción asume que cada byte del arreglo está almacenado
     * como un entero y combina cuatro bytes consecutivos en un entero
     * utilizando operaciones de desplazamiento y máscara para asegurar
     * que se maneje correctamente el formato de bytes big-endian.
     * @param llave el arreglo de bytes de donde se extraen los bytes.
     * @param posicion la posición en el arreglo desde la cual empezar
     *                 la extracción de los cuatro bytes.
     * @return el entero de 32 bits formado por los cuatro bytes
     *         comenzando en la posición especificada. Si se excede el
     *         tamaño del arreglo, se regresa 0.
     */
    private static int extraeEntero(byte[] llave, int posicion) {
        if (posicion < llave.length)
            return (0xFF & llave[posicion]);
        return 0;
    }
    
    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0, posicion = 0;
        while (posicion < llave.length)
            r ^= combina(extraeEntero(llave, posicion++),
                         extraeEntero(llave, posicion++),
                         extraeEntero(llave, posicion++),
                         extraeEntero(llave, posicion++));	
        return r;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a = 0x9E3779B9;
        int b = 0x9E3779B9;
        int c = 0xFFFFFFFF;
        int posicion = 0;
        boolean ejecucion = true;
        while (ejecucion) {
            a += combina(extraeEntero(llave, posicion + 3),
                         extraeEntero(llave, posicion + 2),
                         extraeEntero(llave, posicion + 1),
                         extraeEntero(llave, posicion));
            posicion += 4;
            b += combina(extraeEntero(llave, posicion + 3),
                         extraeEntero(llave, posicion + 2),
                         extraeEntero(llave, posicion + 1),
                         extraeEntero(llave, posicion));
            posicion += 4;
            if (llave.length - posicion >= 4)
                c += combina(extraeEntero(llave, posicion + 3),
                             extraeEntero(llave, posicion + 2),
                             extraeEntero(llave, posicion + 1),
                             extraeEntero(llave, posicion));
            else {
                ejecucion = false;
                c += llave.length;
                c += combina(extraeEntero(llave, posicion + 2),
                             extraeEntero(llave, posicion + 1),
                             extraeEntero(llave, posicion),
                             0);
            }
            posicion += 4;
            
            a -= b + c;
            a ^= (c >>> 13);
            b -= c + a;
            b ^= (a << 8);
            c -= a + b;
            c ^= (b >>> 13);

            a -= b + c;
            a ^= (c >>> 12);
            b -= c + a;
            b ^= (a << 16);
            c -= a + b;
            c ^= (b >>> 5);

            a -= b + c;
            a ^= (c >>> 3);
            b -= c + a;
            b ^= (a << 10);
            c -= a + b;
            c ^= (b >>> 15);
        }
        return c;
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;
        for (int i = 0; i < llave.length; i++)
            h += (h << 5) + extraeEntero(llave, i);
        return h;
    }
}
