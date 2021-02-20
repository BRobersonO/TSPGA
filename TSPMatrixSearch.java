import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixSearch {
    public static void TSPMatrixSearch(FileWriter summaryOutput) throws java.io.IOException { 
    /***Use GetDistance method to populate Matrix based on going from city i (row) to city j (column)***/
        int[][] matrix1 = new int[][]    
        {
            {0,10,20,30},
            {40,0,10,21},
            {21,70,0,50},
            {100,20,31,0}
        };
        
        System.out.println("\nOriginal Matrix");
        Printer.printMatrix(matrix1);
        
        List<Integer> visited = new ArrayList<>(matrix1.length);
    
        //make a pop list
        List<Pathway> population = new ArrayList<>();
        
        int k;
        for(k = 0; k < 5/*POP SIZE*/; k++) {
            Random rand = new Random();
            int x = rand.nextInt(matrix1.length); //our randomly selected start city
            //initialize path to 0's
            int j, jj;
            int[][] path = new int[matrix1.length][matrix1.length];
            for(j = 0; j < path.length; j++) {
                for(jj = 0; jj < path.length; jj++) {
                    path[j][jj] = 0;
                }
            }

            int fitness = 0;
            Pathway newWay = new Pathway(path, fitness);
            
            //clear the cities-visited list
            visited.clear();  
            
            int error = step(matrix1, x, visited, population, newWay);
            if(error == 1) {
                System.out.println("step had an error");
            }
        }
        int h;
        for (h = 0; h < population.size(); h++) {
            Pathway element = population.get(h);
            
            System.out.println("\nCreated Matrix");
            Printer.printMatrix(element.path);
            System.out.println("The fitness of this path is " + element.fitness);
            System.out.println("\n");
        }
        
        Cross.cross(population.get(0),population.get(1), matrix1);
        
    }
    
    public static int step(int [][] matrix, int city, List<Integer> visited, List<Pathway> population, Pathway newWay) {
        if(visited.size() == matrix.length) {
            return 0;
        }
        
        int i;
        int minNotVisited = -1;
        int maxNotVisited = -1;
        boolean m = mutate(); //will have to feed mutation rate into here
        for (i = 0; i < matrix.length; i++) {
            if (city == i) {
                continue;
            }
            if(!hasBeenVisited(i, visited) && minNotVisited == -1) {
                minNotVisited = i;
            }
            if(!hasBeenVisited(i, visited) && maxNotVisited == -1) {
                maxNotVisited = i;
            }
            if(!m && !hasBeenVisited(i, visited) && matrix[city][minNotVisited] > matrix[city][i]) {
                minNotVisited = i;
            }
            if(m && !hasBeenVisited(i, visited) && matrix[city][maxNotVisited] < matrix[city][i]) {
                maxNotVisited = i;
            }
        }
        visited.add(city);
        //change to methods withn objects
        if(minNotVisited == -1 || maxNotVisited == -1) {
            //this is the last city
            if(m) {
                newWay.addFitness(matrix[city][visited.get(0)]);
                newWay.addPath(city, visited.get(0));
                //add to pop list here, by calling method in pop object
                population.add(newWay);
                //recurse here
                return 0;
                //step(matrix, visited.get(0), visited, population, newWay);
            }
            else {
                newWay.addFitness(matrix[city][visited.get(0)]);
                newWay.addPath(city, visited.get(0));
                //add to pop list here, by calling method in pop object
                population.add(newWay);            
                //recurse here
                return 0;
                //step(matrix, visited.get(0), visited, population, newWay);
            }
        }
        else if(m) {
            newWay.addFitness(matrix[city][maxNotVisited]);
            newWay.addPath(city, maxNotVisited);

            //recurse here
            step(matrix, maxNotVisited, visited, population, newWay);
        }
        else {
            newWay.addFitness(matrix[city][minNotVisited]);
            newWay.addPath(city, minNotVisited);
         
            //recurse here
            step(matrix, minNotVisited, visited, population, newWay);
        }
        return 0;
    }
    
    public static boolean hasBeenVisited (int city, List<Integer> visited) {
        return visited.contains(city);
    }
    
    public static boolean mutate(){
        Random rand = new Random();
        int chance = rand.nextInt(10000);
        return chance == 1; //represents a mutation rate of 0.01
    }
}
