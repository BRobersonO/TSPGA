/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

public class Search 
{

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

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
		//Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		new Parameters(args[0]);

		//Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		Parameters.outputParameters(summaryOutput);

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
		
		if (Parameters.problemType.equals("TSPAdj"))
		{
			new TSPAdjSearch(summaryOutput);
		}
		else if (Parameters.problemType.equals("TSPMap"))
		{
			new TSPMapSearch(summaryOutput);
		}
		else if (Parameters.problemType.equals("TSPBlake"))
		{
			new TSPMatrixSearch(summaryOutput);
		}
		else System.out.println("Invalid Problem Type");

	} // End of Main Class

}   // End of Search.Java ******************************************************