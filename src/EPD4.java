import java.util.Random;

public class EPD4 {
    // -------------------------------- [ OBJETOS ESTATICOS ] ----------------------------------------

    /**
     * Objeto reutilizable para generar numeros aletorios
     */
    private static final Random RAND = new Random();

    /**
     * Numero de iteraciones para calcular la media de tiempo que tarda una funcion
     */
    private static final int ITERACIONES = 5;


    // -------------------------------- [ METODOS AYUDA ] ----------------------------------------

    /**
     * Imprime una matriz tabulada en la terminal
     *
     * @param m Matriz que se quiere imprimir
     */
    private static void imprimirMatriz(int[][] m) {
        if (m == null) {
            System.out.println("[ NULL ]");
            return;
        }

        System.out.println("[");
        for (int f = 0; f < m.length; f++) {
            System.out.printf("\t{ ");

            for (int c = 0; c < m[0].length; c++)
                System.out.printf("%3d ", m[f][c]);

            System.out.println("},");
        }
        System.out.println("]");
    }

    /**
     * Calcula la matriz transpuesta
     *
     * @param m Matriz normal
     * @return Matriz transpuesta
     */
    private static int[][] transpuesta(int[][] m) {
        int f = m.length;
        int c = m[0].length;
        int[][] matriz = new int[c][f];

        for (int i = 0; i < f; i++)
            for (int j = 0; j < c; j++)
                matriz[j][i] = m[i][j];

        return matriz;
    }

    /**
     * Genera una matriz de tamaño especificado con numeros aleatorios
     *
     * @param f Numero de filas
     * @param c Numero de columnas
     * @return Matriz Magica
     */
    private static int[][] generarMatrizMagica(int f, int c) {
        int[][] matriz = new int[f][c];

        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                // LLenar la matriz con numeros entre 0 y 999
                matriz[i][j] = RAND.nextInt(1000);
            }
        }

        return matriz;
    }

    /**
     * Calcula un indice especifico al multiplicar de dos matrices
     *
     * @param size Es el size comun que comparten ambas matrices para poder ser multiplicadas (3x'2' * '2'x4)
     * @param a    Matriz A
     * @param b    Matriz B
     * @return C_{c, f} = A{f, size} * B{c, size} + A{f, size-1} * B{c, size-1} + ...
     */
    private static int calcularIndice(int size, int[] a, int[] b) {
        int res = 0;

        for (int s = 0; s < size; s++)
            res += a[s] * b[s];

        return res;
    }

    // -------------------------------- [ METODOS AYUDA ] ----------------------------------------


    // ----------------------------- [ MULTIPLICAR MATRICES ] ------------------------------------

    // a[0].length => columnas
    // b.length    => filas

    /**
     * Multiplica dos matrices
     *
     * @param a Matriz A
     * @param b Matriz B
     * @return A x B
     */
    public static int[][] multiplicarMatricesIT(int[][] a, int[][] b) {
        if (a[0].length != b.length)
            throw new IllegalArgumentException("A debe tener el mismo numero de columnas que filas B");

        int size = b.length;
        int[][] matriz = new int[a.length][b[0].length];
        int[][] bT = transpuesta(b);

        for (int f = 0; f < a.length; f++) {
            for (int c = 0; c < b[0].length; c++) {
                matriz[f][c] = calcularIndice(size, a[f], bT[c]);
            }
        }

        return matriz;
    }

    /**
     * Multiplica dos matrices
     *
     * @param a Matriz A
     * @param b Matriz B
     * @return A x B
     */
    public static int[][] multiplicarMatricesREC(int[][] a, int[][] b) {
        if (a[0].length != b.length)
            throw new IllegalArgumentException("A debe tener el mismo numero de columnas que filas B");
        int[][] matriz = new int[a.length][b[0].length];

        p_multiplicarMatricesREC(matriz, a, b, transpuesta(b), 0, 0);

        return matriz;
    }

    /**
     * Funcion wrapped que verdaderamente multiplica dos matrices de manera recursiva
     *
     * @param m  Matriz sobre la que trabajar
     * @param a  Matriz A
     * @param b  Matriz B
     * @param bT Matriz B traspuesta
     * @param f  Fila
     * @param c  Columna
     */
    private static void p_multiplicarMatricesREC(int[][] m, int[][] a, int[][] b, int[][] bT, int f, int c) {
        // Si nos quedamos sin filas, terminamos
        if (f >= a.length) return;

        // Si nos quedamos sin columnas, pasamos a la siguiente fila
        if (c >= b[0].length) {
            p_multiplicarMatricesREC(m, a, b, bT, f + 1, 0);
            return;
        }

        // Actualizar los valores de la matriz original
        m[f][c] = calcularIndice(b.length, a[f], bT[c]);

        // Calcular siguiente elemento
        p_multiplicarMatricesREC(m, a, b, bT, f, c + 1);
    }

    // ----------------------------- [ MULTIPLICAR MATRICES ] ------------------------------------


    // ------------------------------- [ MEDICION TIEMPOS ] ---------------------------------------

    /**
     * Calcula el tiempo promedio que tarda la funcion multiplicarMatricesIT en calcular cierto volumen de datos
     *
     * @param a Filas de la matriz A
     * @param b Filas de la matriz B y columnas de la matriz A
     * @param c Columnas de la matriz B
     * @return Tiempo promedio
     * @see multiplicarMatricesIT
     */
    private static double calcularMediaTiempoIT(int a, int b, int c) {
        double sumTiempos = 0;

        for (int i = 0; i < ITERACIONES; i++) {
            int[][] x = generarMatrizMagica(a, b);
            int[][] y = generarMatrizMagica(b, c);

            long init = System.nanoTime();

            int[][] res = multiplicarMatricesIT(x, y);

            long end = System.nanoTime();

            sumTiempos += end - init;
        }

        return sumTiempos / ITERACIONES / 1_000_000;
    }

    /**
     * Calcula el tiempo promedio que tarda la funcion multiplicarMatricesREC en calcular cierto volumen de datos
     *
     * @param a Filas de la matriz A
     * @param b Filas de la matriz B y columnas de la matriz A
     * @param c Columnas de la matriz B
     * @return Tiempo promedio
     * @see multiplicarMatricesREC
     */
    private static double calcularMediaTiempoRec(int a, int b, int c) {
        double sumTiempos = 0;

        for (int i = 0; i < ITERACIONES; i++) {
            int[][] x = generarMatrizMagica(a, b);
            int[][] y = generarMatrizMagica(b, c);

            long init = System.nanoTime();

            int[][] res = multiplicarMatricesREC(x, y);

            long end = System.nanoTime();

            sumTiempos += end - init;
        }

        return sumTiempos / ITERACIONES / 1_000_000;
    }

    // ------------------------------- [ MEDICION TIEMPOS ] ---------------------------------------


    public static void main(String[] args) {
        // ------- [ CALENTAMIENTO ] -------
        // https://stackoverflow.com/questions/504103/how-do-i-write-a-correct-micro-benchmark-in-java
        System.out.println("Inicio calentamiento...");
        for (int i = 0; i < 50; i++) {
            calcularMediaTiempoIT(128, 128, 128);
        }
        for (int i = 0; i < 50; i++) {
            calcularMediaTiempoRec(60, 60, 60);
        }
        System.out.println("Fin calentamiento!");


        // ----- [ COSTES TEMPORALES IT ] -----
        int[] sizes = {16, 32, 64, 128, 256, 512, 1024, 2048}; // 4096

        // Cuadradas
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoIT(s, s, s));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
            }
        }

        // Rectangulares
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoIT(s, s / 2, s));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
            }
        }

        // Spaghetti
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoIT(s, 16, s));
            } catch (StackOverflowError e) {
                System.out.printf("%d,error%n", s);
            }
        }

        // Cuadradas y Rectangulares
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoIT(s, s, s * 2));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
            }
        }


        // ----- [ COSTES TEMPORALES REC ] -----
        // Cuadradas
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoRec(s, s, s));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
                break;
            }
        }

        // Rectangulares
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoRec(s, s / 2, s));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
                break;
            }
        }

        // Spaghetti
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoRec(s, 16, s));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
                break;
            }
        }

        // Cuadradas y Rectangulares
        System.out.println("\nSize,Tiempo_ms");
        for (int s : sizes) {
            try {
                System.out.printf("%4d,%.4f%n", s, calcularMediaTiempoRec(s, s, s * 2));
            } catch (StackOverflowError e) {
                System.out.printf("%4d,error%n", s);
                break;
            }
        }

    }
}
