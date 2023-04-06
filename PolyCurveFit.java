import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PolyFitSum {

    public static double[][] matMult(double[][] A, double[][] B) throws IllegalArgumentException {
        if (A[0].length != B.length) throw new IllegalArgumentException("Mismatched Arrays");

        double[][] result = new double[A.length][B[0].length];
        //try catch statement to ensure that the lengths of the array are valid arguments
        // look at diagram on Marker I
        try {
            for (int i = 0; i < A.length; i++) { // pick a row
                for (int j = 0; j < B[0].length; j++) { // pick a col
                    for (int k = 0; k < B.length; k++) {
                        result[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
            
            return result;

        } catch (Exception e){
            throw new IllegalArgumentException("Missmatched Arrays");
        }
    }

    public static double[][] invert(double[][] A) throws IllegalArgumentException {
        if (A.length != A[0].length) {
            throw new IllegalArgumentException("Matrix is not Invertible");
        }

        double[][] I = new double[A.length][A[0].length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (i == j) I[i][j] = 1.0;
            }
        }

        for (int i = 0; i < A.length; i++) {
            int pivotLoc = i;
            double pivot = A[pivotLoc][i];
            try {
                while (Math.abs(A[pivotLoc][i]) < 0.0000000001) { // prevents FloatingPointException
                    ++pivotLoc;
                }
                pivot = A[pivotLoc][i];
                swapRows(A, i, pivotLoc);
                swapRows(I, i, pivotLoc);
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] /= pivot;
                    I[i][j] /= pivot;
                }
                
                for (int ii = 0; ii < A.length; ii++) {
                    if (i == ii) continue;
                    double multiplier = A[ii][i];
                    for (int j = 0; j < A[0].length; j++) {
                        A[ii][j] -= multiplier * A[i][j];
                        I[ii][j] -= multiplier * A[i][j];
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Singular Matrix");
            }
        }

        return I;
    }

    private static int getSize(File f) throws FileNotFoundException {
        int count = 0;
        Scanner s = new Scanner(f);
        while (s.hasNext()) {
            count++;
            s.nextLine();
        }
        s.close();
        return count;
    }

    private static void swapRows(double[][] A, int r0, int r1) {
        for (int j = 0; j < A[0].length; j++) {
            double temp = A[r0][j];
            A[r0][j] = A[r1][j];
            A[r1][j] = temp;
        }
    }

    public static void printMatrix(double[][] A) {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                System.out.printf("%7.2f", A[i][j]);
            }
            System.out.println();
        }
    }

    public static double evalPoly(double[] coefs, double x) {
        double result = 0;
        double xPower = 1;
        for (int i = 0; i < coefs.length; i++) {
            result += (coefs[i] * xPower);
            xPower *= x; 
        }
        return result;
    }

    public static double hornersMethod(double[] coefs, double x) { // Uses factorization, halves the amount of multiplication

        int n = coefs.length;
        double result = coefs[n-1];

        for (int i = n-2; i >= 0; i--) {
            result *= x; 
            result += coefs[i];
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        
        File f = new File("NoisyPolynomialData.csv");

        for (int k = 1; k <= 7; k++) {

            double[][] Q = new double[1+k][1+k];
            double[][] U = new double[1+k][1];

            int N = getSize(f);
            Q[0][0] = N;

            Scanner s = new Scanner(f);
            double[][] io = new double[N][N];
            int count = 0;

            while (s.hasNext()) {
                String[] vals = s.nextLine().split(",");
                double x = Double.parseDouble(vals[0]);
                double y = Double.parseDouble(vals[1]);
                io[count][0] = x;
                io[count++][1] = y;
    
                // fill Q
                for (int row = 0; row <= k; row++) {
                    for (int col = 0; col <= k; col++) {
                        Q[row][col] += Math.pow(x, row+col);
                    }
                }

    
                // fill U
                for (int row = 0; row <= k; row++) {
                    Q[row][0] += y*Math.pow(x, row);
                }
            }
    
            double[][] inverseQ = invert(Q);
            double[][] ans = matMult(inverseQ, U);
            double[] c = new double[k];
            for (int row = 0; row < k; row++) { // need to fix this to read the data properly
                c[row] = ans[row][0];
            }
            
    
            
            System.out.println("Degree " + k + ": \n");
            for (int i = 0; i < N; i++) {
                System.out.println(hornersMethod(c, io[i][0]));
            }
            System.out.println();
            
            printMatrix(ans);
    
            s.close();
        }
        
    }
}
