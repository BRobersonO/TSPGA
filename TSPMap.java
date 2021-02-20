import java.io.*;
import java.util.*;

//Class to handle all the TSPMap calculations
public class TSPMap extends FitnessFunction
{
    //List of all the cities from the file
    public static Map<Integer, Position> citiesMap = new HashMap<Integer, Position>();
    
    //Constructor to automatically call the file input methods using the filename given by the parameter fill
    public TSPMap() throws java.io.IOException
    {
        name = "Traveling Salesman Problem";
        CityMapData mapData = new CityMapData(Parameters.dataInputFileName);
        citiesMap = mapData.citiesMap;
    }
    
    //RawFitness scoreing method
    public void doRawFitness(ChromoMap x)
    {
        x.rawFitness = 0;

        Position prevChromo = null;
        
        for (Object o : x.chromo)
        {
            if(prevChromo != null)
            {
                x.rawFitness += prevChromo.GetDistance(citiesMap.get(o));
            }
            
            prevChromo = citiesMap.get(o);
        }
        
        x.rawFitness += prevChromo.GetDistance(citiesMap.get(1));
    }
    
    //Prints a single chromos genes
    public void doPrintGenes(ChromoMap chromo, FileWriter output) throws java.io.IOException
    {
        for (int i = 0; i < Parameters.numGenes; i++)
        {
            Hwrite.right(chromo.chromo.get(i).toString(), 11, output);
        }
        
        output.write("   RawFitness");
        output.write("\n        ");
        
        for (int i = 0; i < Parameters.numGenes; i++)
        {
            Hwrite.right(chromo.chromo.get(i), 11, output);
        }
        
        Hwrite.right((int) chromo.rawFitness, 13, output);
        output.write("\n\n");
        return;
    }
}
