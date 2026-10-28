import java.util.ArrayList;
import java.util.List;
import java.util.Random;

record Fila(String size, double tiempoIterativo, double tiempoRecursivo) {
}

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
            System.out.print("\t{ ");

            for (int c = 0; c < m[0].length; c++)
                System.out.printf("%3d ", m[f][c]);

            System.out.println("},");
        }
        System.out.println("]");
    }

    private static void imprimirCSV(String titulo, List<Fila> filas) {
        System.out.println(titulo);
        System.out.println("Size,TiempoIterativo,TiempoRecursivo");

        for (Fila f : filas)
            System.out.printf("%s, %.4f, %.4f", f.size(), f.tiempoIterativo(), f.tiempoRecursivo());
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

    /**
     * Mide el tiempo en calcular dos matrices AxB * BxC
     */
    private static Fila medir(int a, int b, int c) {
        String size = String.format("%dx%d * %dx%d", a, b, b, c); // AxB * BxC

        double it = calcularMediaTiempoIT(a, b, c);
        double rec;
        try {
            rec = calcularMediaTiempoRec(a, b, c);
        } catch (StackOverflowError e) {
            rec = Double.NaN;
        }

        return new Fila(size, it, rec);
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
        p_multiplicarMatricesREC(matriz, a, b, transpuesta(b), 0, 0, 0);

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
     * @param k  Indice actual de la suma
     */
    private static void p_multiplicarMatricesREC(int[][] m, int[][] a, int[][] b, int[][] bT, int f, int c, int k) {
        // Si nos quedamos sin filas, terminamos
        if (f >= a.length) return;

        // Si nos quedamos sin columnas, pasamos a la siguiente fila
        if (c >= b[0].length) {
            p_multiplicarMatricesREC(m, a, b, bT, f + 1, 0, 0);
            return;
        }

        // Si nos quedamos sin numeros, pasamos a la siguiente columna
        if (k >= b.length) {
            p_multiplicarMatricesREC(m, a, b, bT, f, c + 1, 0);
            return;
        }

        // Actualizar los valores de la matriz original
        m[f][c] += a[f][k] * bT[c][k];

        // Calcular siguiente elemento
        p_multiplicarMatricesREC(m, a, b, bT, f, c, k + 1);
    }

    /**
     * Multiplica dos matrices ( Sin StackOverflow por ahora :] )
     *
     * @param a Matriz A
     * @param b Matriz B
     * @return A x B
     */
    public static int[][] multiplicarMatricesRECv2(int[][] a, int[][] b) {
        if (a[0].length != b.length)
            throw new IllegalArgumentException("A debe tener el mismo numero de columnas que filas B");

        int[][] matriz = new int[a.length][b[0].length];
        f1(matriz, a, transpuesta(b), 0, 0);

        return matriz;
    }

    /**
     * Divide la matriz en filas
     *
     * @param m  Puntero a la matriz resultado
     * @param a  Matriz A
     * @param bT Matriz B traspuesta
     * @param f  Fila
     * @param c  Columna
     */
    private static void f1(int[][] m, int[][] a, int[][] bT, int f, int c) {
        // Si nos quedamos sin filas terminamos
        if (f >= a.length) return;

        // Calcular esta fila
        f2(m, a[f], bT, f, c);

        // Pasar a la siguiente
        f1(m, a, bT, f + 1, c);
    }

    /**
     * Divide filas en columnas
     *
     * @param m  Puntero a la matriz resultado
     * @param a  Matriz A
     * @param bT Matriz B traspuesta
     * @param f  Fila
     * @param c  Columna
     */
    private static void f2(int[][] m, int[] a, int[][] bT, int f, int c) {
        // Si nos quedamos sin columnas terminamos
        if (c >= bT.length) return;

        // Rellenar la celda de esta columna
        f3(m, a, bT, f, c, 0);

        // Pasar a la siguiente columna
        f2(m, a, bT, f, c + 1);
    }

    /**
     * Rellena las columnas de una fila
     *
     * @param m  Puntero a la matriz resultado
     * @param a  Matriz A
     * @param bT Matriz B traspuesta
     * @param f  Fila
     * @param c  Columna
     * @param k  Indice actual de la suma
     */
    private static void f3(int[][] m, int[] a, int[][] bT, int f, int c, int k) {
        // Si nos quedamos sun numeros, terminamos
        if (k >= bT[0].length) return;

        // Calcular celda actual
        m[f][c] += a[k] * bT[c][k];

        // Siguiente celda
        f3(m, a, bT, f, c, k + 1);
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

            int[][] res = multiplicarMatricesRECv2(x, y);

            long end = System.nanoTime();

            sumTiempos += end - init;
        }

        return sumTiempos / ITERACIONES / 1_000_000;
    }

    // ------------------------------- [ MEDICION TIEMPOS ] ---------------------------------------


    public static void main(String[] args) {
        // ------- [ CALENTAMIENTO ] -------
        // https://stackoverflow.com/questions/504103/how-do-i-write-a-correct-micro-benchmark-in-java
        // Rule 1 y 5
        System.out.println("Inicio calentamiento...");
        for (int i = 0; i < 100; i++)
            calcularMediaTiempoIT(512, 512, 512);

        for (int i = 0; i < 100; i++)
            calcularMediaTiempoRec(512, 512, 512);

        System.out.println("Fin calentamiento!");


        // ----- [ COSTES TEMPORALES ] -----
        int[] sizes = {16, 32, 64, 128, 256, 512, 1024, 2048};
        String cabecera = "\nSize,TiempoIterativo,TiempoRecursivo";

        List<Fila> cuadradas = new ArrayList<>(sizes.length);
        List<Fila> rectangulares = new ArrayList<>(sizes.length);
        List<Fila> spaghetti = new ArrayList<>(sizes.length);
        List<Fila> cuadradaRect = new ArrayList<>(sizes.length);

        for (int s : sizes) {
            cuadradas.add(medir(s, s, s));
            rectangulares.add(medir(s, s / 2, s));
            spaghetti.add(medir(s, 16, s));
            cuadradaRect.add(medir(s, s, s * 2));
        }

        imprimirCSV("Cuadradas", cuadradas);
        imprimirCSV("Rectangulares", rectangulares);
        imprimirCSV("Spaghetti", spaghetti);
        imprimirCSV("Cuadrada x Rectangular", cuadradaRect);
    }
}
