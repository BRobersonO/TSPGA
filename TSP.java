import java.util.*;

//Class to handle all the TSP calculations
public class TSP extends FitnessFunction
{
    //List of all the cities from the file
    public static List<City> cities = new ArrayList<City>();

    //Constructor to automatically call the file input methods using the filename given by the parameter file
    public TSP() throws java.io.IOException
    {
		name = "Traveling Salesman Problem";

        CityFileData tspFileData = new CityFileData(Parameters.dataInputFileName);

        cities = tspFileData.cities;
    }

    //Note: TSP solutions here just like OneMax.java
}
