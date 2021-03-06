import java.io.*;
import java.util.*;
import java.text.*;

public class TSPMatrixCross {
    public static void cross(TSPMatrixPathway p1, TSPMatrixPathway p2, double[][] dataMatrix, List<TSPMatrixPathway> newPop) {
        //take starting cities, make sure not the same city
        int city1 = p1.firstCity;
        int city2 = p2.firstCity;
        
        if(city1 == city2) {
            city2 = (city2 + 1) % dataMatrix.length;
        }

        List<Integer> visited = new ArrayList<>(dataMatrix.length);
        
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
                parent2 = (parent2 + 1) % population.size();
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
        population.clear();
        population.addAll(newPop);
        newPop.clear();
    }

    public static boolean isCrossing(double crossRate){
        Random rand = new Random();
        int chance = rand.nextInt(100);
        return chance < (crossRate * 100); 
    }
    
}
class SortTheFitness implements Comparator<TSPMatrixPathway>
{
    // Sort a list ascending order by fitness
    public int compare(TSPMatrixPathway o1, TSPMatrixPathway o2) {
        return Double.compare(o1.fitness, o2.fitness);
    }
}

