import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixCross {
    public static void cross(TSPMatrixPathway p1, TSPMatrixPathway p2, double[][] dataMatrix, List<TSPMatrixPathway> newPop) {
        //take starting cities, make sure not the same city
        int city1 = p1.firstCity;
        int city2 = p2.firstCity;
    //     if(city1 == -1) {
    //         System.out.println("city1error");
    //     }
    //    if(city2 == -1) {
    //         System.out.println("city2error");
    //     }
        
        if(city1 == city2) {
            city2 = p2.firstCity++ % dataMatrix.length;
        }
    //    if(city2 == -1) {
    //         System.out.println("city2errorfindsecond");
    //     }
        
        List<TSPMatrixPathway> population = new ArrayList<>();
        List<Integer> visited = new ArrayList<>(dataMatrix.length);
        
        // int k;
        // for(k = 0; k < 1/*POP SIZE*/; k++) {
        //     Random rand = new Random();
        //     int x = rand.nextInt(dataMatrix.length); //our randomly selected start city
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
            
            
            TSPMatrixPathway newWay = new TSPMatrixPathway(path, fitness, city1);
            

            
            visited.add(city1);
        
            TSPMatrixSearch.step(dataMatrix, city2, visited, newPop, newWay);

    }

    public static void crossO (double crossRate, List<TSPMatrixPathway> population, double[][] dataMatrix) {
        Random rand = new Random();
        List<TSPMatrixPathway> newPop = new ArrayList<>();

        //elitism, put best of pop into newpop
        List<TSPMatrixPathway> sortedPop = population;
        sortedPop.sort(new SortTheFitness());
        newPop.add(sortedPop.get(0));

        while(newPop.size() < population.size()){
            int parent1 = rand.nextInt(population.size()); // get first parent
            int parent2 = rand.nextInt(population.size());// get second parent
            // make sure parents are unique
            if(parent1 == parent2) {
                parent2 = parent2++ % population.size();
            }
            if(isCrossing(crossRate)){
                //add the child
                cross(population.get(parent1), population.get(parent2), dataMatrix, newPop);
            }
            else {
                //add the best parent instead
                newPop.add( population.get(parent1).fitness >= population.get(parent2).fitness ? population.get(parent1) : population.get(parent2));
            }

        }
        population = newPop;
    }

    public static boolean isCrossing(double crossRate){
        Random rand = new Random();
        int chance = rand.nextInt(100);
        return chance < (crossRate * 100); 
    }
  
    // public static int findFirst(int[][] matrix) {
    //     int i;
    //     for(i = 0; i < matrix.length; i++) {
    //         if(matrix[i][0] == 1) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
    
    // public static int findSecond(int[][] matrix) {
    //     int i;
    //     for(i = 0; i < matrix.length; i++) {
    //         if(matrix[i][1] == 1) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
    
}
class SortTheFitness implements Comparator<TSPMatrixPathway>
{
    // Sort a list ascending order by fitness
    public int compare(TSPMatrixPathway o1, TSPMatrixPathway o2) {
        double x = o1.fitness - o2.fitness;
        return x < 0 ? -1 : 1;
    }

