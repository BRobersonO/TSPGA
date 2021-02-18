

import java.io.*;
import java.util.*;
import java.text.*;
import java.text.DecimalFormat;

public class TSPAdjSearch{

	/*******************************************************************************
	 *                           STATIC VARIABLES                                   *
	 *******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;
	public static Chromo maxChromo;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static double bestofRunGAverage;          //the average generation the best was found over all runs
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumOfSquaresBestFit;
	public static double sumOfSquaresBestFitGen;
	public static double sumOfSquaresAvgFit;
	public static double sumOfSquaresAvgFitPerGen[];
	public static double sumOfSquaresPerGenBestFit[];
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double averageBestFitOverRuns;   //the average of the best fitness from all runs
	public static double averageRunFitness;
	public static double averageAvgFitnessOverRuns;        // the average of the average fitness from all runs
	public static double stdevPerGenBest;
	public static double stdevRawFitness;
	public static double stdevBestGen;              //stdev of the girst gen the best fitness was found over all the runs
	public static double stdevBestFitOverRuns;      //stdev of the best fitness over all the runs
	public static double stdevAvgFitOverRuns;       //stdev of the average fitness over all the runs
	public static double stdevOptimalGen;

	public static double averageBestFit95CI[];   //the 95% confidence interval for best fitness
	public static double averageAvgFit95CI[];    //the 95% confidence interval
	public static double averageBestFitGen95CI[];
	public static double OptimalChromo95Interval;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];  // 0=Avg, 1=Best

	private static DecimalFormat df2 = new DecimalFormat("#.##"); //allows me to force 2 decimal places
	public static double optimalChrmosomeGeneration[];
	public static double OptimalChromoGenAverage;
	public static double sumOfSquaresOptimalGen;
	public static int numOfOptimals;
	public static CityAdjData coords;


	/*******************************************************************************
	 *                             STATIC METHODS                                   *
	 *******************************************************************************/

	public static void main(String[] args) throws java.io.IOException {


		Calendar dateAndTime = Calendar.getInstance();
		Date startTime = dateAndTime.getTime();

		//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);
		coords = new CityAdjData(new String("berlin52.tsp"));

		//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);



		//		Set up Fitness Statistics matrix
		fitnessStats = new double[2][Parameters.generations];
		sumOfSquaresAvgFitPerGen = new double[Parameters.generations];
		sumOfSquaresPerGenBestFit = new double[Parameters.generations];
		optimalChrmosomeGeneration = new double[Parameters.generations];
		OptimalChromoGenAverage = 0;
		for (int i=0; i<Parameters.generations; i++){
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
			sumOfSquaresPerGenBestFit[i] = 0;
			sumOfSquaresAvgFitPerGen[i] = 0;
			optimalChrmosomeGeneration[i] = -1;
		}

		//	Problem Specific Setup - For new new fitness function problems, create
		//	the appropriate class file (extending FitnessFunction.java) and add
		//	an else_if block below to instantiate the problem.

		if (Parameters.problemType.equals("NM")){
			//problem = new NumberMatch();
		}
		else if (Parameters.problemType.equals("OM")){
			//problem = new OneMax();
		}
		else if (Parameters.problemType.equals("TSPBin")) {
			problem = new TSPAdj();
		}
		else if (Parameters.problemType.equals("TSPMap")) {

		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);



		//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();
		//maxChromo = new Chromo(1);

		averageBestFitOverRuns = 0.0;
		bestofRunGAverage = 0.0;
		sumOfSquaresBestFit = 0.0;
		sumOfSquaresBestFitGen = 0.0;
		stdevBestGen = 0.0;
		stdevBestFitOverRuns = 0.0;
		stdevAvgFitOverRuns = 0.0;
		stdevOptimalGen = 0.0;
		averageRunFitness = 0.0;
		sumOfSquaresAvgFit = 0.0;
		sumOfSquaresOptimalGen = 0.0;
		numOfOptimals = 0;
		OptimalChromo95Interval = 0.0;
		averageBestFit95CI = new double[2];
		averageBestFit95CI[0] = 0.0;
		averageBestFit95CI[1] = 0.0;
		averageAvgFit95CI = new double[2];
		averageAvgFit95CI[0] = 0.0;
		averageAvgFit95CI[1] = 0.0;
		averageBestFitGen95CI = new double[2];
		averageBestFitGen95CI[0] = 0.0;
		averageBestFitGen95CI[1] = 0.0;

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;
		//maxChromo.rawFitness = 0;
		//problem.doRawFitness(maxChromo);


		//loops the runs and does the prep work for a run.
		for (R = 1; R <= Parameters.numRuns; R++){
			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				member[i] = new Chromo();
				child[i] = new Chromo();
			}

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){
				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				bestOfGenChromo.rawFitness = defaultBest;
				// Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){
					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;

					problem.doRawFitness(member[i], coords);
					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 + (member[i].rawFitness * member[i].rawFitness);

					if (Parameters.minORmax.equals("max")){
						if (member[i].rawFitness > bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}//end of fitness test

				// Accumulate fitness statistics
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;
				sumOfSquaresPerGenBestFit[G] = sumOfSquaresPerGenBestFit[G] + (bestOfGenChromo.rawFitness * bestOfGenChromo.rawFitness);
				sumOfSquaresAvgFitPerGen[G] = sumOfSquaresAvgFitPerGen[G] +((sumRawFitness / Parameters.popSize) * (sumRawFitness / Parameters.popSize));

				averageRawFitness = sumRawFitness / Parameters.popSize;
				averageRunFitness += averageRawFitness;
				stdevRawFitness = Math.sqrt(
						Math.abs(sumRawFitness2 -
								sumRawFitness*sumRawFitness/Parameters.popSize)
						/
						(Parameters.popSize-1)
						);

				// Output generation statistics to screen
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\n");

				// *********************************************************************
				// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
				// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = member[i].rawFitness + .000001;
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = 1/(member[i].rawFitness + .000001);
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=Parameters.popSize-1; i>0; i--){
						for (int j=0; j<i; j++){
							if (memberFitness[j] > memberFitness[j+1]){
								TmemberIndex = memberIndex[j];
								TmemberFitness = memberFitness[j];
								memberIndex[j] = memberIndex[j+1];
								memberFitness[j] = memberFitness[j+1];
								memberIndex[j+1] = TmemberIndex;
								memberFitness[j+1] = TmemberFitness;
							}
						}
					}
					//  Copy ordered array to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=1; i<Parameters.popSize; i++){
						for (int j=(Parameters.popSize - 1); j>=i; j--){
							if (memberFitness[j-i] < memberFitness[j]){
								TmemberIndex = memberIndex[j-1];
								TmemberFitness = memberFitness[j-1];
								memberIndex[j-1] = memberIndex[j];
								memberFitness[j-1] = memberFitness[j];
								memberIndex[j] = TmemberIndex;
								memberFitness[j] = TmemberFitness;
							}
						}
					}
					//  Copy array order to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}

				// *********************************************************************
				// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
				// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member[i].proFitness = member[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}


				// *********************************************************************
				// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
				// *********************************************************************

				int parent1 = -1;
				int parent2 = -1;

				//  Assumes always two offspring per mating
				for (int i=0; i<Parameters.popSize; i=i+2){

					//	Select Two Parents
					parent1 = Chromo.selectParent();
					parent2 = parent1;
					while (parent2 == parent1){
						parent2 = Chromo.selectParent();
					}

					//	Crossover Two Parents to Create Two Children
					randnum = r.nextDouble();
					if (randnum < Parameters.xoverRate){
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], coords);
						parent1 = Chromo.selectParent();
						parent2 = parent1;
						while (parent2 == parent1){
							parent2 = Chromo.selectParent();
						}
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i+1], coords);
					}
					else {
						Chromo.mateParents(parent1, member[parent1], child[i]);
						Chromo.mateParents(parent2, member[parent2], child[i+1]);
					}
				} // End Crossover

			}// end of run
		}//end of all runs







		//CityBinData test = new CityBinData(new String("berlin52.tsp"));
		//problem.doRawFitness(bestOverAllChromo, test);

		System.out.println("it works?");
	}

	public static void BinaryMain(CityAdjData Coords){

	}

}
