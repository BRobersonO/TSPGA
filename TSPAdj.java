import java.io.*;
import java.util.*;
import java.text.*;



public class TSPAdj extends FitnessFunction{
	
	/*******************************************************************************
	*                              CONSTRUCTORS                                    *
	*******************************************************************************/

		public TSPAdj(){
			name = "TSPBin Problem";
		}

	/*******************************************************************************
	*                                MEMBER METHODS                                *
	*******************************************************************************/
		
		public void doRawFitness(ChromoAdj X, CityAdjData Coords) {
			int city1 = 0;
			int city2 = X.chromo[city1];
			int pathLength = Parameters.numGenes;
			double distance = 0;
			
			for(int i=0; i < pathLength; i++) {
				distance = Position.GetDistance((float)Coords.citiesMap[city1][0],(float)Coords.citiesMap[city2][0],(float)Coords.citiesMap[city1][1],(float)Coords.citiesMap[city2][1]);
				X.rawFitness += distance;
				city1 = city2;
				city2 = X.chromo[city1];
			}
			
		}
}
