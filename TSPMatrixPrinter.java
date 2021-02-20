import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixPrinter {
    public static void printMatrix (int[][] matrix) {
        
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }
}

