import java.io.*;
import java.util.*;

//Main class to read in the city data
public class CityMapData
{
    //File information for displaying purposes
    public static String Name;
    public static String FileType;
    public static String Comment;
    public static int Dimension;
    public static String EDGE_WEIGHT_TYPE;
    
    //Collection of cities needed for the GA's
    public Map<Integer, Position> citiesMap = new HashMap<Integer, Position>();
    
    //Constructor that will bring in the data file needed for the variables
    //Note: This will need to be called in another class like NumberMatch.java is doing
    public CityMapData(String filename) throws java.io.IOException
    {
        //Scanner to read in the file
        Scanner scanner = new Scanner(new File(filename));
        boolean dataSection = false;
        
        //Loops through each line in the scanner until the end of the file
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            
            //checks if the line just read in is the EOF line all the tsp files have.
            if (line.equals("EOF")) { break; }
            
            if (line.contains("NODE_COORD_SECTION") || dataSection)
            {
                if (!dataSection)
                {
                    line = scanner.nextLine();
                    dataSection = true;
                }
                
                //Splitting on whitespace characters
                String[] cityVars = line.split("\\s");
                
                //Assigning the proper data
                int cityID = Integer.parseInt(cityVars[0]);
                float cityX = Float.parseFloat(cityVars[1]);
                float cityY = Float.parseFloat(cityVars[2]);
                
                //Creating and adding in new city as they are read in from the file
                citiesMap.put(cityID, new Position(cityX, cityY));
            }
        }
    }
    
    //Output method for the file info
    public static void outputCityFileData(FileWriter output) throws java.io.IOException
    {
        output.write("Name                   :  " + Name + "\n");
        output.write("Type                   :  " + FileType + "\n");
        output.write("Comment                :  " + Comment + "\n");
        output.write("Dimension              :  " + Dimension + "\n");
        output.write("EDGE_WEIGHT_TYPE       :  " + EDGE_WEIGHT_TYPE + "\n");
        
        output.write("\n\n");
    }
}
