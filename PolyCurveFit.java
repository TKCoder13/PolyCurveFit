import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PolyCurveFit {

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
        if (A.length != A[0].length) throw new IllegalArgumentException("Matrix is not Invertible");

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
                swapRows(A, i, pivotLoc);
                swapRows(I, i, pivotLoc);
                pivot = A[pivotLoc][i];
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] /= pivot;
                    I[i][j] /= pivot;
                }
                
                for (int ii = 0; ii < A.length; ii++) {
                    if (i == ii) continue;
                    double multiplier = A[ii][i];
                    for (int j = 0; j < A[0].length; j++) {
                        A[ii][j] -= multiplier * A[i][j];
                        I[ii][j] -= multiplier * I[i][j]; // mistakes were made here, never forget.
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Singular Matrix");
            }
        }

        return I;
    }

    private static int getSize(File file) throws FileNotFoundException {
        int count = 0;
        Scanner s = new Scanner(file);
        while (s.hasNext()) {
            count++;
            s.next();
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

    public static double evalPoly(double[] coefs, double x) { // Same amt of adds and multis, but got rid of Math.pow()
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


        int N = getSize(f)/2; // for some reason, N is initially double the size
        

        Scanner s = new Scanner(f);
        double[][] io = new double[N][N];
        int count = 0;

        while (s.hasNext()) {
            String[] vals = s.nextLine().split(",");
            io[count][0] = Double.parseDouble(vals[0]);
            io[count++][1] = Double.parseDouble(vals[1]);
        }
        s.close();

        for (int deg = 1; deg <= 7; deg++) {

            // inc in size as the poly gets bigger (explained in class)
            double[][] Q = new double[deg+1][deg+1];
            double[][] U = new double[deg+1][1]; 

            Q[0][0] = N;

            for (int i = 0; i < io.length; i++) {
                double x = io[i][0];
                double y = io[i][1];

                // this is needed to create polys, not just lines
                // I used Coleman's and Reif's portion of the code here, as I was stuck
                double[] exps = new double[2*(deg+1)];
                exps[0] = 1;
                for (int e = 1; e < exps.length; e++) {
                    exps[e] = exps[e-1] * x;
                }

                for (int row = 0; row < Q.length; row++) {
                    // Input data into Q
                    for (int col = 0; col < Q[0].length; col++) {
                        if (row != 0 && col != 0) {
                            Q[row][col] += exps[row + col];
                        }
                    }

                    // Input data into U
                    U[row][0] += y * exps[row];
                }
            }
            
            Q = invert(Q);
            double[][] ans = matMult(Q, U);
            double[] a = new double[ans.length];

            for (int coef = 0; coef < a.length; coef++) {
                a[coef] = ans[coef][0];
            }

            System.out.println("\nDegree " + deg);
            for (int i = 0; i < N; i++) {
                System.out.println(hornersMethod(a, io[i][0])); //   + " \tgiven (x, y): (" + io[i][0] + ", " + io[i][1] + ")"
            }
            printMatrix(ans);
        }
    }
}
