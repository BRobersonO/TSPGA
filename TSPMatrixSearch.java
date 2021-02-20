import java.io.*;
import java.util.*;
import java.text.*;


public class TSPMatrixSearch extends FitnessFunction{

    public static FitnessFunction problem;

	public static ChromoMap[] memberMap;
	public static ChromoMap[] childMap;

	public static ChromoMap bestOfGenChromoMap;
	public static int bestOfGenR;
	public static int bestOfGenG;

	public static ChromoMap bestOfRunChromoMap;
	public static int bestOfRunR;
	public static int bestOfRunG;

	public static ChromoMap bestOverAllChromoMap;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;
	public static double ninetyFiveLow = 0;
	public static double ninetyFiveHigh = 0;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];  // 0=Avg, 1=Best, 2=sum of squares of avgs, 3=sum of sqs of bests

	public static int bestEaRunSum = 0;
	public static int bestEaRunSum2 = 0;
	public static double bestEaRunAvg = 0;
	public static double stDvR = 0; //stdv from best in each run
	public static double bestEaLow = 0;
	public static double bestEaHigh = 0;

	public static double stDvA = 0;
	public static double stDvB = 0;
	public static double conIntLow = 0;
	public static double conIntHigh = 0;

    public TSPMatrixSearch(FileWriter summaryOutput) throws java.io.IOException  { 

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

        name = "Traveling Salesman Problem";

        new CityMatrixData(Parameters.dataInputFileName);

        double[][] matrix1 = CityMatrixData.citiesMatrix;
        
        System.out.println("\nOriginal Matrix");
        //TSPMatrixPrinter.printMatrix(matrix1);

        //Set up Fitness Statistics matrix
		fitnessStats = new double[4][Parameters.generations];
		for (int i=0; i<Parameters.generations; i++)
		{
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
			fitnessStats[2][i] = 0;
			fitnessStats[3][i] = 0;
		}

        Parameters.numGenes = CityMatrixData.cities.size();
        
        List<Integer> visited = new ArrayList<>(matrix1.length);
    
        //make a pop list
        List<TSPMatrixPathway> population = new ArrayList<>();
        
        int k;
        for(k = 0; k < Parameters.popSize/*POP SIZE*/; k++) {
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

            double fitness = 0;
            TSPMatrixPathway newWay = new TSPMatrixPathway(path, fitness);
            
            //clear the cities-visited list
            visited.clear();  
            
            int error = step(matrix1, x, visited, population, newWay);
            if(error == 1) {
                System.out.println("step had an error");
            }
        } // Population created

        

        int h;
        for (h = 0; h < population.size(); h++) {
            TSPMatrixPathway element = population.get(h);
            
            System.out.println("\nCreated Matrix");
            //TSPMatrixPrinter.printMatrix(element.path);
            System.out.println("The fitness of this path is " + element.fitness);
            System.out.println("\n");
        }
        
        TSPMatrixCross.cross(population.get(0),population.get(1), matrix1);
        
    }
    
    public static int step(double [][] matrix, int city, List<Integer> visited, List<TSPMatrixPathway> population, TSPMatrixPathway newWay) {
        if(visited.size() == matrix.length) {
            return 0;
        }
        
        int i;
        int minNotVisited = -1;
        int maxNotVisited = -1;
        boolean m = mutate(Parameters.mutationRate); //will have to feed mutation rate into here
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
    
    public static boolean mutate(double mutRate){
        Random rand = new Random();
        int chance = rand.nextInt(100);
        return chance < (mutRate * 100); //represents a mutation rate of mutRate
    }
}
