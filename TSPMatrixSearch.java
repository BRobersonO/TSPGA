import java.io.*;
import java.util.*;
import java.text.*;


public class TSPMatrixSearch extends FitnessFunction{

    public static FitnessFunction problem;

    public static double defaultF = 999999999;

	public static double bestOfGen = defaultF;
	public static double bestOfRun = defaultF;
	public static double bestOfAll = defaultF;

    public static TSPMatrixPathway bestOfGenM;
	public static TSPMatrixPathway bestOfRunM;
	public static TSPMatrixPathway bestOfAllM;

	public static double sumRawFitness = 0;
	public static double sumRawFitness2 = 0;	// sum of squares of fitness


	public static double averageRawFitness = 0;
	public static double stdevRawFitness = 0;
	public static double ninetyFiveLow = 0;
	public static double ninetyFiveHigh = 0;

	public static int G;
	public static int R;

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
        summaryOutput.write("\n");
        System.out.println("\n");

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

        name = "Traveling Salesman Problem";

        new CityMatrixData(Parameters.dataInputFileName);

        double[][] matrix1 = CityMatrixData.citiesMatrix;
        //Set up Fitness Statistics matrix
		fitnessStats = new double[4][Parameters.generations];
		for (int i=0; i<Parameters.generations; i++) // 0=Avg, 1=Best, 2=sum of squares of avgs, 3=sum of sqs of bests
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

        //BEGIN RUNS
        for (R = 0; R < Parameters.numRuns; R++) 
        {
            visited.clear();
            population.clear();
            bestOfRun = defaultF;

            InitialPopulation(matrix1, visited, population); //creates init pop

            //BEGIN GENERATIONS
            for (G = 0; G < Parameters.generations; G++) 
            {
                bestOfGen = defaultF;

                //Crossover produces new modified population
                TSPMatrixCross.crossO (Parameters.xoverRate, population, matrix1);
                
                List<TSPMatrixPathway> sortedPop = population;
                sortedPop.sort(new SortTheFitness());
                bestOfGen = (sortedPop.get(0).fitness);
                bestOfGenM = sortedPop.get(0);

                for(int i = 0; i < population.size(); i++) {
                    sumRawFitness += population.get(i).fitness;
                    sumRawFitness2 += (population.get(i).fitness * population.get(i).fitness);
                }

                // Accumulate fitness statistics ****
                // 0=Avg, 1=Best, 2=sum of squares of avgs, 3=sum of sqs of bests
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGen;
				fitnessStats[2][G] += (sumRawFitness / Parameters.popSize) * (sumRawFitness / Parameters.popSize);
				fitnessStats[3][G] += bestOfGen * bestOfGen;

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
				System.out.println(R + "\t" + G +  "\t" + bestOfGen + "\t" + averageRawFitness + "\t" + stdevRawFitness);
			
				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write("\t\t G ");
				Hwrite.right(G, 3, summaryOutput);
				summaryOutput.write("\t\t BestInG ");
				Hwrite.right(bestOfGen, 11, 3, summaryOutput);
				summaryOutput.write("\t\t AvgOfG ");
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\t\t StDv ");
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\t\t 95% ConfInt ");
				Hwrite.right(ninetyFiveLow, 11, 3, summaryOutput);
				Hwrite.right(ninetyFiveHigh, 11, 3, summaryOutput);
				summaryOutput.write("\n");

                bestOfRun = bestOfRun < bestOfGen ? bestOfRun : bestOfGen;
                bestOfRunM = bestOfRun < bestOfGen ? bestOfRunM : bestOfGenM;


            }//  Repeat the above loop for each generation
            //END OF GENERATIONS
            Hwrite.left("Best of Run:", 8, summaryOutput);
            
            summaryOutput.write("\n");
            Hwrite.left(bestOfRun, 4, summaryOutput);
            summaryOutput.write("\n");

			bestEaRunSum += bestOfRun;
			bestEaRunSum2 += bestOfRun * bestOfRun;

            //PRINTS OUT FULL BINARY MATRIX
			//TSPMatrixPrinter.printMatrix(bestOfRunM.path);

			System.out.println("\n" + R + "\t" + "Best of Run" + "\t"+ bestOfRun + "\n");
            bestOfAll = bestOfAll < bestOfRun ? bestOfAll : bestOfRun;
            bestOfAllM = bestOfAll < bestOfRun ? bestOfAllM : bestOfRunM;
        }
        //END OF RUNS

        Hwrite.left("Best of All Runs", 8, summaryOutput);
        summaryOutput.write("\n");
        Hwrite.left(bestOfAll, 4, summaryOutput);
        summaryOutput.write("\n");

        //PRINTS OUT FULL BINARY MATRIX
        //TSPMatrixPrinter.printMatrix(bestOfAllM.path);

        System.out.println(R + "\t" + "Best of All" + "\t"+ bestOfAll);

		//	Output Fitness Statistics matrix
		summaryOutput.write("Gen            AvgOfAvgFit            AvgOfBestFit            StDvAvg            StDvAvgBst           95%ConfIntBst\n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i]/Parameters.numRuns, 20, 2, summaryOutput);

			stDvA = Math.sqrt(
					Math.abs(
							fitnessStats[2][i] - fitnessStats[0][i] * fitnessStats[0][i] / Parameters.numRuns
					)
					/
					Parameters.numRuns
			);

			stDvB = Math.sqrt(
					Math.abs(
							fitnessStats[3][i] - fitnessStats[1][i] * fitnessStats[1][i] / Parameters.numRuns
					)
							/
							Parameters.numRuns
			);

			Hwrite.left(stDvA, 20, 2, summaryOutput);
			Hwrite.left(stDvB, 20, 2, summaryOutput);

			conIntLow =  (fitnessStats[1][i] / Parameters.numRuns) - (stDvB / Math.sqrt(Parameters.numRuns)) * 2;
			conIntHigh =  (fitnessStats[1][i] / Parameters.numRuns) + (stDvB / Math.sqrt(Parameters.numRuns)) * 2;

			Hwrite.left(conIntLow, 20, 2, summaryOutput);
			Hwrite.left(conIntHigh, 20, 2, summaryOutput);

			summaryOutput.write("\n");



		}
		//Best of Each Run Output
		summaryOutput.write("\n");

		bestEaRunAvg = bestEaRunSum / Parameters.numRuns;

		stDvR = Math.sqrt(
				Math.abs(
						bestEaRunSum2 - bestEaRunSum * bestEaRunSum / Parameters.numRuns
				)
						/
						Parameters.numRuns
		);

		bestEaLow = bestEaRunAvg - (stDvR / Math.sqrt(Parameters.numRuns)) * 2;
		bestEaHigh = bestEaRunAvg + (stDvR / Math.sqrt(Parameters.numRuns)) * 2;

		summaryOutput.write("Best Each Run: Average		");
		Hwrite.left(bestEaRunAvg, 20, 2, summaryOutput);
		summaryOutput.write("Best Each Run: StDv	");
		Hwrite.left(stDvR, 20, 2, summaryOutput);
		summaryOutput.write("Best Each Run: 95%ConInt	");
		Hwrite.left(bestEaLow, 20, 2, summaryOutput);
		Hwrite.left(bestEaHigh, 20, 2, summaryOutput);

		summaryOutput.write("\n");
		summaryOutput.close();

		System.out.println();

        //PRINT OUT BEST PATHWAY


        System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
    }
    //*******************METHODS***********/
    //*************************************/
    public static int step(double [][] matrix, int city, List<Integer> visited, List<TSPMatrixPathway> population, TSPMatrixPathway newWay) {
        if(visited.size() == matrix.length) {
            return 0;
        }
        int minNotVisited = -1;
        int maxNotVisited = -1;
        boolean m = mutate(Parameters.mutationRate); //will have to feed mutation rate into here
        for (int i = 0; i < matrix.length; i++) {
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
        if(minNotVisited == -1 || maxNotVisited == -1) {
            //this is the last city
            if(m) {
                newWay.addFitness(matrix[city][visited.get(0)]);
                newWay.addPath(city, visited.get(0));
                population.add(newWay);
                return 0;
            }
            else {
                newWay.addFitness(matrix[city][visited.get(0)]);
                newWay.addPath(city, visited.get(0));
                population.add(newWay);
                return 0;
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
            TSPMatrixPathway newWay = new TSPMatrixPathway(path, fitness, x);
            
            //clear the cities-visited list
            visited.clear();  
            //build the PathWay
            step(matrix1, x, visited, population, newWay);
        } 
    }
}