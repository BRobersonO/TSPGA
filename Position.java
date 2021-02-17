//Object to hold the x and y position in the map
public class Position 
{
    //Variables needed for the GA's
    public float X;
    public float Y;

    //Constructor for the Position object to assign the variables needed
    public Position(float x_cord, float y_cord)
    {
        this.X = x_cord;
        this.Y = y_cord;
    }

    //Calculate the distance from the current position to any given position object
    public double GetDistance(Position otherPos)
    {
        return Math.sqrt(Math.pow((otherPos.X - this.X), 2) + Math.pow((otherPos.Y - this.Y), 2));
    }

    //Calculate the distance between four floats passed in
    public static double GetDistance(float x1, float x2, float y1, float y2)
    {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
    
    //Calculate the distance between two positions passed in
    public static double GetDistance(Position first, Position second)
    {
        return Math.sqrt(Math.pow((second.X - first.X), 2) + Math.pow((second.Y - first.Y), 2));
    }
}
