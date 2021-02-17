/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static ChromoMap[] memberMap;
	public static ChromoMap[] childMap;
	public static Chromo[] member;
	public static Chromo[] child;

	public static ChromoMap bestOfGenChromoMap;
	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;

	public static ChromoMap bestOfRunChromoMap;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;

	public static ChromoMap bestOverAllChromoMap;
	public static Chromo bestOverAllChromo;
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
	private static int bestEaRun[];

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



/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void main(String[] args) throws java.io.IOException
	{
		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

		//Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);

		//Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);

		//Set up Fitness Statistics matrix
		fitnessStats = new double[4][Parameters.generations];
		for (int i=0; i<Parameters.generations; i++)
		{
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
			fitnessStats[2][i] = 0;
			fitnessStats[3][i] = 0;
		}

		//Problem Specific Setup - For new fitness function problems, create
		//the appropriate class file (extending FitnessFunction.java) and add
		//an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("NM"))
		{
			problem = new NumberMatch();
			RunSolution(summaryOutput);
		} 
		else if (Parameters.problemType.equals("OM")) 
		{
			problem = new OneMax();
			RunSolution(summaryOutput);
		} 
		else if (Parameters.problemType.equals("TSPMap")) 
		{
			problem = new TSPMap();
			Parameters.numGenes = TSPMap.citiesMap.size();
			System.out.println("Running Solution");
			RunMapSolution(summaryOutput);
		} 
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

		OutputCreation(dateAndTime, startTime, summaryOutput);

	} // End of Main Class

	private static void OutputCreation(Calendar dateAndTime, Date startTime, FileWriter summaryOutput) throws IOException 
	{
			Hwrite.left("B", 8, summaryOutput);

			if (Parameters.problemType.equals("TSPMap"))
			{
				problem.doPrintGenes(bestOverAllChromoMap, summaryOutput);
			}
			else
			{
				problem.doPrintGenes(bestOverAllChromo, summaryOutput);
			}

			// Output Fitness Statistics matrix
			summaryOutput.write(
					"Gen            AvgOfAvgFit            AvgOfBestFit            StDvAvg            StDvAvgBst           95%ConfIntBst\n");
			for (int i = 0; i < Parameters.generations; i++) {
				Hwrite.left(i, 15, summaryOutput);
				Hwrite.left(fitnessStats[0][i] / Parameters.numRuns, 20, 2, summaryOutput);
				Hwrite.left(fitnessStats[1][i] / Parameters.numRuns, 20, 2, summaryOutput);

				stDvA = Math.sqrt(
						Math.abs(fitnessStats[2][i] - fitnessStats[0][i] * fitnessStats[0][i] / Parameters.numRuns)
								/ Parameters.numRuns);

				stDvB = Math.sqrt(
						Math.abs(fitnessStats[3][i] - fitnessStats[1][i] * fitnessStats[1][i] / Parameters.numRuns)
								/ Parameters.numRuns);

				Hwrite.left(stDvA, 20, 2, summaryOutput);
				Hwrite.left(stDvB, 20, 2, summaryOutput);

				conIntLow = (fitnessStats[1][i] / Parameters.numRuns) - (stDvB / Math.sqrt(Parameters.numRuns)) * 2;
				conIntHigh = (fitnessStats[1][i] / Parameters.numRuns) + (stDvB / Math.sqrt(Parameters.numRuns)) * 2;

				Hwrite.left(conIntLow, 20, 2, summaryOutput);
				Hwrite.left(conIntHigh, 20, 2, summaryOutput);

				summaryOutput.write("\n");
			}

			// Best of Each Run Output
			summaryOutput.write("\n");

			bestEaRunAvg = bestEaRunSum / Parameters.numRuns;

			stDvR = Math.sqrt(
					Math.abs(bestEaRunSum2 - bestEaRunSum * bestEaRunSum / Parameters.numRuns) / Parameters.numRuns);

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
			System.out.println("Start:  " + startTime);
			dateAndTime = Calendar.getInstance();
			Date endTime = dateAndTime.getTime();
			System.out.println("End  :  " + endTime);
		}

	private static void RunSolution(FileWriter summaryOutput) throws IOException
	{
		//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		//  Start program for multiple runs
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

				//	Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					member[i].rawFitness = 0;

					problem.doRawFitness(member[i]);

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
						member[i].rawFitness * member[i].rawFitness;

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
				}

				// Accumulate fitness statistics ****
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;
				fitnessStats[2][G] += (sumRawFitness / Parameters.popSize) * (sumRawFitness / Parameters.popSize);
				fitnessStats[3][G] += bestOfGenChromo.rawFitness * bestOfGenChromo.rawFitness;

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
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write("\t\t G ");
				Hwrite.right(G, 3, summaryOutput);
				summaryOutput.write("\t\t BestInG ");
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
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
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
					}
					else {
						Chromo.mateParents(parent1, member[parent1], child[i]);
						Chromo.mateParents(parent2, member[parent2], child[i+1]);
					}
				} // End Crossover

				//	Mutate Children
				for (int i=0; i<Parameters.popSize; i++){
					child[i].doMutation();
				}

				//	Swap Children with Last Generation
				for (int i=0; i<Parameters.popSize; i++){
					Chromo.copyB2A(member[i], child[i]);
				}

			} //  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			//bestEaRun[R] = bestOfRunR;
			bestEaRunSum += (int)bestOfRunChromo.rawFitness;
			bestEaRunSum2 += (int)bestOfRunChromo.rawFitness * (int)bestOfRunChromo.rawFitness;

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);

		} //End of a Run
	}

	private static void RunMapSolution(FileWriter summaryOutput) throws IOException
	{
		//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		memberMap = new ChromoMap[Parameters.popSize];
		childMap = new ChromoMap[Parameters.popSize];
		bestOfGenChromoMap = new ChromoMap();
		bestOfRunChromoMap = new ChromoMap();
		bestOverAllChromoMap = new ChromoMap();

		if (Parameters.minORmax.equals("max"))
		{
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else
		{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromoMap.rawFitness = defaultBest;

		//  Start program for multiple runs
		for (R = 1; R <= Parameters.numRuns; R++)
		{
			bestOfRunChromoMap.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i = 0; i < Parameters.popSize; i++)
			{
				memberMap[i] = new ChromoMap();
				childMap[i] = new ChromoMap();
			}

			for (G = 0; G < Parameters.generations; G++) 
			{
				sumRawFitness = 0;
				sumRawFitness2 = 0;

				bestOfGenChromoMap.rawFitness = defaultBest;

				//	Test Fitness of Each Member
				for (int i = 0; i < Parameters.popSize; i++)
				{
					memberMap[i].rawFitness = 0;
					problem.doRawFitness(memberMap[i]);

					sumRawFitness += memberMap[i].rawFitness;
					sumRawFitness2 += memberMap[i].rawFitness * memberMap[i].rawFitness;

					if (Parameters.minORmax.equals("max")) 
					{
						if (memberMap[i].rawFitness > bestOfGenChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOfGenChromoMap, memberMap[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (memberMap[i].rawFitness > bestOfRunChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOfRunChromoMap, memberMap[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (memberMap[i].rawFitness > bestOverAllChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOverAllChromoMap, memberMap[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else 
					{
						if (memberMap[i].rawFitness < bestOfGenChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOfGenChromoMap, memberMap[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (memberMap[i].rawFitness < bestOfRunChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOfRunChromoMap, memberMap[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (memberMap[i].rawFitness < bestOverAllChromoMap.rawFitness)
						{
							ChromoMap.copyB2A(bestOverAllChromoMap, memberMap[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}

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

				switch(Parameters.scaleType)
				{
					case 0:     // No change to raw fitness
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberMap[i].sclFitness = memberMap[i].rawFitness + .000001;
							sumSclFitness += memberMap[i].sclFitness;
						}
						break;

					case 1:     // Fitness not scaled.  Only inverted.
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberMap[i].sclFitness = 1/(memberMap[i].rawFitness + .000001);
							sumSclFitness += memberMap[i].sclFitness;
						}
						break;

					case 2:     // Fitness scaled by Rank (Maximizing fitness)

						//  Copy genetic data to temp array
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberIndex[i] = i;
							memberFitness[i] = memberMap[i].rawFitness;
						}
						//  Bubble Sort the array by floating point number
						for (int i=Parameters.popSize-1; i>0; i--)
						{
							for (int j=0; j<i; j++)
							{
								if (memberFitness[j] > memberFitness[j+1])
								{
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
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberMap[memberIndex[i]].sclFitness = i;
							sumSclFitness += memberMap[memberIndex[i]].sclFitness;
						}

						break;

					case 3:     // Fitness scaled by Rank (minimizing fitness)

						//  Copy genetic data to temp array
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberIndex[i] = i;
							memberFitness[i] = memberMap[i].rawFitness;
						}

						//  Bubble Sort the array by floating point number
						for (int i=1; i<Parameters.popSize; i++)
						{
							for (int j=(Parameters.popSize - 1); j>=i; j--)
							{
								if (memberFitness[j-i] < memberFitness[j])
								{
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
						for (int i=0; i<Parameters.popSize; i++)
						{
							memberMap[memberIndex[i]].sclFitness = i;
							sumSclFitness += memberMap[memberIndex[i]].sclFitness;
						}

						break;

					default:
						System.out.println("ERROR - No scaling method selected");
				}

				
			// *********************************************************************
			// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
			// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++)
				{
					memberMap[i].proFitness = memberMap[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + memberMap[i].proFitness;
				}

			// // *********************************************************************
			// // ************ CROSSOVER AND CREATE NEXT GENERATION *******************
			// // *********************************************************************

			// 	int parent1 = -1;
			// 	int parent2 = -1;

			// 	//  Assumes always two offspring per mating
			// 	for (int i=0; i<Parameters.popSize; i=i+2)
			// 	{
			// 		//	Select Two Parents
			// 		parent1 = ChromoMap.selectParent();
			// 		parent2 = parent1;

			// 		while (parent2 == parent1)
			// 		{
			// 			parent2 = ChromoMap.selectParent();
			// 		}

			// 		//	Crossover Two Parents to Create Two Children
			// 		randnum = r.nextDouble();
			// 		if (randnum < Parameters.xoverRate)
			// 		{
			// 			ChromoMap.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
			// 		}
			// 		else 
			// 		{
			// 			ChromoMap.mateParents(parent1, member[parent1], child[i]);
			// 			ChromoMap.mateParents(parent2, member[parent2], child[i+1]);
			// 		}
			// 	} // End Crossover

			// 	//	Mutate Children
			// 	for (int i=0; i<Parameters.popSize; i++)
			// 	{
			// 		child[i].doMutation();
			// 	}

			// 	//	Swap Children with Last Generation
			// 	for (int i=0; i<Parameters.popSize; i++)
			// 	{
			// 		ChromoMap.copyB2A(member[i], child[i]);
			// 	}
			}//  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			//bestEaRun[R] = bestOfRunR;
			bestEaRunSum += (int)bestOfRunChromoMap.rawFitness;
			bestEaRunSum2 += (int)bestOfRunChromoMap.rawFitness * (int)bestOfRunChromoMap.rawFitness;

			problem.doPrintGenes(bestOfRunChromoMap, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromoMap.rawFitness);

		} //End of a Run
	}

}   // End of Search.Java ******************************************************

