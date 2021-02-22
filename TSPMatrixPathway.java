import java.io.*;
import java.util.*;
import java.text.*;

public class Pathway {
    int [][] path;
    int fitness;
    int firstCity;
    
    public Pathway (int[][] path, int fitness, int firstCity) {
        this.path = path;
        this.fitness = fitness;
        this.firstCity = firstCity;
    }
    
    public void addFitness(int x) {
        this.fitness += x;
    }
    
    public void addPath(int i, int j) {
        this.path[i][j] = 1;
    }
}