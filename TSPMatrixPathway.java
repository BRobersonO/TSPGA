import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixPathway {
    int[][] path;
    double fitness;
    int firstCity;

    public TSPMatrixPathway(int[][] path, double fitness, int firstCity) {
        this.path = path;
        this.fitness = fitness;
        this.firstCity = firstCity;
    }
    
    public void addFitness(double x) {
        this.fitness += x;
    }
    
    public void addPath(int i, int j) {
        this.path[i][j] = 1;
    }
}