import java.io.*;
import java.util.*;

//Class to handle all the TSPMap calculations
public class TSPMap extends FitnessFunction
{
    //List of all the cities from the file
    public static Map<Integer, Position> citiesMap = new HashMap<Integer, Position>();

    //Constructor to automatically call the file input methods using the filename given by the parameter file
    public TSPMap() throws java.io.IOException
    {
        name = "Traveling Salesman Problem";

        CityMapData mapData = new CityMapData(Parameters.dataInputFileName);

        citiesMap = mapData.citiesMap;
    }

	public void doRawFitness()
    {

	}

	public void doPrintGenes(ChromoMap chromo, FileFilter outputFile) throws java.io.IOException
    {

	}
}
