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

        //Looping through the number of runs
        for (int r = 0; r < Parameters.numRuns; r++) 
        {
            InitialPopulation(matrix1, visited, population);

            //Looping through the generations
            //TODO: Get the best fit, Calculate the crossover and apply to the new generation
            for (int g = 0; g < Parameters.generations; g++) 
            {
                //Get the best fit for the generation
                //Get the best fit for the run
                //Get the best fit for the overall

                // Accumulate fitness statistics ****
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromoMap.rawFitness;
				fitnessStats[2][G] += (sumRawFitness / Parameters.popSize) * (sumRawFitness / Parameters.popSize);
				fitnessStats[3][G] += bestOfGenChromoMap.rawFitness * bestOfGenChromoMap.rawFitness;

                averageRawFitness = sumRawFitness / Parameters.popSize;
				stdevRawFitness = Math.sqrt(
							Math.abs(sumRawFitness2 - 
							sumRawFitness*sumRawFitness/Parameters.popSize)
							/
							(Parameters.popSize-1)
							);

				ninetyFiveLow = averageRawFitness - (stdevRawFitness / Math.sqrt(Parameters.popSize)) * 2;
				ninetyFiveHigh = averageRawFitness + (stdevRawFitness / Math.sqrt(Parameters.popSize)) * 2;

				// Output generation statistics to screen
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromoMap.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);
			
				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write("\t\t G ");
				Hwrite.right(G, 3, summaryOutput);
				summaryOutput.write("\t\t BestInG ");
				Hwrite.right((int)bestOfGenChromoMap.rawFitness, 7, summaryOutput);
				summaryOutput.write("\t\t AvgOfG ");
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\t\t StDv ");
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\t\t 95% ConfInt ");
				Hwrite.right(ninetyFiveLow, 11, 3, summaryOutput);
				Hwrite.right(ninetyFiveHigh, 11, 3, summaryOutput);
				summaryOutput.write("\n");

			// *********************************************************************
			// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
			// *********************************************************************

                // No change to raw fitness
                for (int i=0; i<Parameters.popSize; i++)
                {
                    memberMap[i].sclFitness = memberMap[i].rawFitness + .000001;
                    sumSclFitness += memberMap[i].sclFitness;

                    // PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM
                    memberMap[i].proFitness = memberMap[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + memberMap[i].proFitness;
                }

            // // *********************************************************************
			// // ************ CROSSOVER AND CREATE NEXT GENERATION *******************
			// // *********************************************************************

				// int parent1 = -1;
				// int parent2 = -1;

				// //  Assumes always two offspring per mating
				// for (int i=0; i<Parameters.popSize; i=i+2)
				// {
				// 	//	Select Two Parents
				// 	parent1 = ChromoMap.selectParent();
				// 	parent2 = parent1;

				// 	while (parent2 == parent1)
				// 	{
				// 		parent2 = ChromoMap.selectParent();
				// 	}

				// 	//	Crossover Two Parents to Create Two Children
				// 	randnum = r.nextDouble();
				// 	if (randnum < Parameters.xoverRate)
				// 	{
				// 		ChromoMap.mateParents(memberMap[parent1], memberMap[parent2], childMap[i], childMap[i+1]);
				// 	}
				// 	else 
				// 	{
				// 		ChromoMap.mateParents(memberMap[parent1], childMap[i]);
				// 		ChromoMap.mateParents(memberMap[parent2], childMap[i+1]);
				// 	}
				// } // End Crossover

                // //	Mutate Children
				// for (int i=0; i<Parameters.popSize; i++)
				// {
				// 	//System.out.println(childMap[i].chromo.size());
				// 	childMap[i].doMutation();
				// }

				// //	Swap Children with Last Generation
				// for (int i=0; i<Parameters.popSize; i++)
				// {
				// 	ChromoMap.copyB2A(memberMap[i], childMap[i]);
				// }
            }//  Repeat the above loop for each generation

            Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			bestEaRunSum += (int)bestOfRunChromoMap.rawFitness;
			bestEaRunSum2 += (int)bestOfRunChromoMap.rawFitness * (int)bestOfRunChromoMap.rawFitness;

			problem.doPrintGenes(bestOfRunChromoMap, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromoMap.rawFitness);
        }

        //We now have a population
        //we need to measure fitness, produce report on this generation
        //we need to do crossover, then select who's in, who's out.
        //repeat -> produce a new generation?

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

    private void InitialPopulation(double[][] matrix1, List<Integer> visited, List<TSPMatrixPathway> population)
    {
        for(int k = 0; k < Parameters.popSize/*POP SIZE*/; k++) {
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
        } // Population creation
    }
}
