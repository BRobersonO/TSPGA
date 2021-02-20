import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixPathway {
    int [][] path;
    double fitness;
    
    public TSPMatrixPathway (int[][] path, double fitness) {
        this.path = path;
        this.fitness = fitness;
    }
    
    public void addFitness(double x) {
        this.fitness += x;
    }
    
    public void addPath(int i, int j) {
        this.path[i][j] = 1;
    }
}
