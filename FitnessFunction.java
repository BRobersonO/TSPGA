/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

class FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public String name;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public FitnessFunction() {

		System.out.print("Setting up Fitness Function.....");

	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(ChromoMap X){
		System.out.println("Executing FF Raw Fitness Map");
	}
	public void doRawFitness(ChromoAdj X){
		System.out.println("Executing FF Raw Fitness");
	}
	
	public void doRawFitness(ChromoAdj X, CityAdjData test){
		System.out.println("Executing FF Raw Fitness");
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(ChromoMap X, FileWriter output) throws java.io.IOException{
		System.out.println("Executing FF Gene Output Map");
	}
	public void doPrintGenes(ChromoAdj X, FileWriter output) throws java.io.IOException{
		System.out.println("Executing FF Gene Output");
	}


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/


}   // End of OneMax.java ******************************************************

