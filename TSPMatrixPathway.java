import java.io.*;
import java.util.*;
import java.text.*;

public class Pathway {
    int [][] path;
    double fitness;
    
    public Pathway (int[][] path, double fitness) {
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
