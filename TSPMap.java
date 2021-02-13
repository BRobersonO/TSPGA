import java.util.*;

//Class to handle all the TSPMap calculations
public class TSPMap extends FitnessFunction
{
    //List of all the cities from the file
    public static HashMap<Position, Integer> citiesMap = new HashMap<Position, Integer>();

    //Constructor to automatically call the file input methods using the filename given by the parameter file
    public TSPMap() throws java.io.IOException
    {
        name = "Traveling Salesman Problem";

        CityMapData mapData = new CityMapData(Parameters.dataInputFileName);

        citiesMap = mapData.map;
    }

    //Note: TSP solutions here just like OneMax.java
}
