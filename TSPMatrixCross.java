import java.io.*;
import java.util.*;
import java.text.*;

public class Cross {
    public static void cross(Pathway p1, Pathway p2, int[][] dataMatrix) {
        //take starting cities, make sure not the same city
        int city1 = findFirst(p1.path);
        int city2 = findFirst(p2.path);
        if(city1 == -1) {
            System.out.println("city1error");
        }
       if(city2 == -1) {
            System.out.println("city2error");
        }
        
        if(city1 == city2) {
            city2 = findSecond(p2.path);
        }
       if(city2 == -1) {
            System.out.println("city2errorfindsecond");
        }
        
        List<Pathway> population = new ArrayList<>();
        List<Integer> visited = new ArrayList<>(dataMatrix.length);
        
        int k;
        for(k = 0; k < 1/*POP SIZE*/; k++) {
            Random rand = new Random();
            int x = rand.nextInt(dataMatrix.length); //our randomly selected start city
            //initialize path to 0's
            int j, jj;
            int[][] path = new int[dataMatrix.length][dataMatrix.length];
            for(j = 0; j < path.length; j++) {
                for(jj = 0; jj < path.length; jj++) {
                    path[j][jj] = 0;
                }
            }
            
            int fitness = 0;
            
            path[city1][city2] = 1;
            fitness += dataMatrix[city1][city2];
            
            
            Pathway newWay = new Pathway(path, fitness);
            

            
            visited.add(city1);
        
            DoTheThing.step(dataMatrix, city2, visited, population, newWay);
        }
        int h;
        //for (h = 0; h < population.size(); h++) {
            Pathway element = population.get(0);
            
            System.out.println("\nCrossed Matrix");
            Printer.printMatrix(element.path);
            System.out.println("The fitness of this path is " + element.fitness);
            System.out.println("\n");
        //}
    }
  
    public static int findFirst(int[][] matrix) {
        int i;
        for(i = 0; i < matrix.length; i++) {
            if(matrix[i][0] == 1) {
                return i;
            }
        }
        return -1;
    }
    
    public static int findSecond(int[][] matrix) {
        int i;
        for(i = 0; i < matrix.length; i++) {
            if(matrix[i][1] == 1) {
                return i;
            }
        }
        return -1;
    }
    
}
