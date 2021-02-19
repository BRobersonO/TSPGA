/******************************************************************************
 *  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
 *  Version 2, January 18, 2004
 *******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class ChromoAdj
{
	/*******************************************************************************
	 *                            INSTANCE VARIABLES                                *
	 *******************************************************************************/

	public int[] chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

	/*******************************************************************************
	 *                            Class VARIABLES                                *
	 *******************************************************************************/

	private static int randnum;
	private static double randnumDouble;
	//used to flip a city from unused to used to prevent invalid paths.
	private final static int used = 1;
	//allows the program to take advantage of pre-filling with 0s 
	private static int Array_Bound = Parameters.numGenes-1;

	/*******************************************************************************
	 *                              CONSTRUCTORS                                    *
	 *******************************************************************************/

	public ChromoAdj(){
		int[] verify = new int[Parameters.numGenes];
		Arrays.fill(verify, 0);
		verify[0] = used;
		int prevRandNum = 0;
		//  Set gene values to a randum sequence cities
		this.chromo = new int[Parameters.numGenes];
		for (int i=0; i<Array_Bound; i++){
			randnum = TSPAdjSearch.r.nextInt(Parameters.numGenes);
			while(verify[randnum] == used) {
				if(randnum == Array_Bound) {
					randnum = 0;
					continue;
				}
				randnum++;
			}
			this.chromo[prevRandNum] = randnum;
			verify[randnum] = used;
			prevRandNum = randnum;
		}

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}



	//creates chromo with max gene
	public ChromoAdj(int x){
		/*
		 * char geneBit; //chromo = ""; for (int i=0; i<Parameters.numGenes; i++){ for
		 * (int j=0; j<Parameters.geneSize; j++){ geneBit = '1'; //this.chromo = chromo
		 * + geneBit; } }
		 * 
		 * this.rawFitness = -1; // Fitness not yet evaluated this.sclFitness = -1; //
		 * Fitness not yet scaled this.proFitness = -1; // Fitness not yet
		 * proportionalized
		 */	}


	/*******************************************************************************
	 *                                MEMBER METHODS                                *
	 *******************************************************************************/
	/*
	//  Get Alpha Represenation of a Gene **************************************
	public String getGeneAlpha(int geneID){
		int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	//  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****

	public int getIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneSign;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=1; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		geneSign = geneAlpha.charAt(0);
		if (geneSign == '1') geneValue = geneValue - (int)Math.pow(2.0, Parameters.geneSize-1);
		return (geneValue);
	}

	//  Get Integer Value of a Gene (Positive only) ****************************

	public int getPosIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=0; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		return (geneValue);
	}
	 */
	//  Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation(){
		//check if mutating
		if (randnum < Parameters.mutationRate){
			//create needed structures
			int[] verify = new int[Parameters.numGenes];
			int[] mutChromo = new int[Parameters.numGenes];
			Arrays.fill(verify, 0);
			//verify[0] = used;
			int newCity;
			
			//loop through the chromosome building a mutated chromo
			for(int i = 0; i < Parameters.numGenes; i++) {
				//get current traveling destination and adjust it to the mutated destination
				if(this.chromo[i]+1 > Array_Bound) {
					newCity = 0;
				}else {
					newCity = this.chromo[i]+1;
				}
				//check new destination to prevent premature loops
				while(verify[newCity] == 1 || newCity == i) {
					newCity++;
					if(newCity > Array_Bound) {
						newCity = 0;
						System.out.println("am Stuck?");
					}
				}

				//now insert new destination.
				mutChromo[i] = newCity;
				verify[newCity] = 1;
			}
			this.chromo = mutChromo;
		}

	}


	/*******************************************************************************
	 *                             STATIC METHODS                                   *
	 *******************************************************************************/

	//  Select a parent for crossover ******************************************

	public static int selectParent(){

		double rWheel = 0;
		int j = 0;
		int k = 0;

		switch (Parameters.selectType){

		case 1:     // Proportional Selection
			randnumDouble = TSPAdjSearch.r.nextDouble();
			for (j=0; j<Parameters.popSize; j++){
				rWheel = rWheel + TSPAdjSearch.member[j].proFitness;
				if (randnumDouble < rWheel) return(j);
			}
			break;

		case 3:     // Random Selection
			randnumDouble = TSPAdjSearch.r.nextDouble();
			j = (int) (randnumDouble * Parameters.popSize);
			return(j);

		case 2:     //  Tournament Selection
			int winner =  TSPAdjSearch.r.nextInt(Parameters.popSize);
			/*int challenger = TSPAdjSearch.r.nextInt(Parameters.popSize);
			while(winner == challenger){
				challenger = TSPAdjSearch.r.nextInt(Parameters.popSize);
			}
			for(int i=0; i < Parameters.numChallengers; i++){
				if(TSPAdjSearch.member[winner].rawFitness < TSPAdjSearch.member[challenger].rawFitness){ //change to a percentage gamble.
					winner = challenger;
				}else if(challenger == winner){
					i-=1;
				}
				challenger = TSPAdjSearch.r.nextInt(Parameters.popSize);
				while(winner == challenger){
					challenger = TSPAdjSearch.r.nextInt(Parameters.popSize);
				}
			}*/
			return winner;
			/*if(Search.member[contestant1].rawFitness > Search.member[contestant2].rawFitness){
				return contestant1;
			} else{
				return contestant2;
			}*/

		default:
			System.out.println("ERROR - No selection method selected");
		}
		return(-1);
	}

	//  Produce a new child from two parents  **********************************

	public static void mateParents(int pnum1, int pnum2, ChromoAdj parent1, ChromoAdj parent2, ChromoAdj child, CityAdjData coords){

		int[] verify = new int[Parameters.numGenes];
		Arrays.fill(verify, 0);
		verify[0] = used;
		int startCity = 0;
		int parent1End;
		int parent2End;
		double distance1 = 0;
		double distance2 = 0;

		//perform the crossover
		for(int i = 0; i < Array_Bound; i++) {
			//find where startCity goes in each parent.
			parent1End = parent1.chromo[startCity];
			parent2End = parent2.chromo[startCity];

			//get the distance from that city to the next city in both parents.
			distance1 = Position.GetDistance((float)coords.citiesMap[startCity][0],(float)coords.citiesMap[parent1End][0],(float)coords.citiesMap[startCity][1],(float)coords.citiesMap[parent1End][1]);
			distance2 = Position.GetDistance((float)coords.citiesMap[startCity][0],(float)coords.citiesMap[parent2End][0],(float)coords.citiesMap[startCity][1],(float)coords.citiesMap[parent2End][1]);

			if(distance1 <= distance2 && verify[parent1End] == 0) {
				child.chromo[startCity] = parent1End;
				verify[parent1End] = 1;
				startCity = parent1End;
			}else if(verify[parent2End] == 0) {
				child.chromo[startCity] = parent2End;
				verify[parent2End] = 1;
				startCity = parent2End;
			} else {
				if(verify[parent1End] == 0) {
					child.chromo[startCity] = parent1End;
					verify[parent1End] = 1;
					startCity = parent1End;
				} else {
					randnum = TSPAdjSearch.r.nextInt(Parameters.numGenes);
					while(verify[randnum] == used) {
						if(randnum == Array_Bound) {
							randnum = 0;
							continue;
						}
						randnum++;
					}
					child.chromo[startCity] = randnum;
					verify[randnum] = 1;
					startCity = randnum;
				}//end final else
			}//end inserting next city
		}//end crossover loop




		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Produce a new child from a single parent  ******************************

	public static void mateParents(int pnum, ChromoAdj parent, ChromoAdj child){

		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (ChromoAdj targetA, ChromoAdj sourceB){

		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

}   // End of Chromo.java ******************************************************