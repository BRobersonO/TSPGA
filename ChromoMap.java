import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChromoMap extends Chromo
{
	public List<Integer> chromo;
	public double rawFitness;

    private static double randnum;

    public ChromoMap()
    {
		//Sets the initial chromo list with the keys from the citiesMap
        chromo = new ArrayList<Integer>(TSPMap.citiesMap.keySet());

        //Shuffles the chromo list to randomize the values
        Collections.shuffle(chromo);

		this.rawFitness = -1;   //  Fitness not yet evaluated
	}

    
	//Mutate a Chromosome Based on Mutation Type - Still needs work
	public void doMutation()
    {
		List<Integer> mutChromo;
		Integer x;

		switch (Parameters.mutationType){

		case 1:     //  Replace with new random number
			break;

		default:
			System.out.println("ERROR - No mutation method selected");
		}
	}

	//Selection of a parent for crossover
	public static int selectParent()
    {
		double rWheel = 0;
		int j = 0;
		int k = 0;

		switch (Parameters.selectType)
        {
            case 1:     // Proportional Selection
                randnum = Search.r.nextDouble();

                for (j=0; j<Parameters.popSize; j++)
                {
                    rWheel = rWheel + Search.member[j].proFitness;
                    if (randnum < rWheel) return(j);
                }

                break;

            case 3:     // Random Selection
                randnum = Search.r.nextDouble();
                j = (int) (randnum * Parameters.popSize);

                return(j);

            case 2:     //  Tournament Selection (Binary)
                randnum = Search.r.nextDouble();
                j = (int) (randnum * Parameters.popSize);
                randnum = Search.r.nextDouble();
                k = (int) (randnum * Parameters.popSize);

                if (Search.member[j].proFitness > Search.member[k].proFitness) return(j);
                else return(k);

            default:
                System.out.println("ERROR - No selection method selected");
		}

	    return(-1);
	}

	//Creates a new child from two selected chromos from the population - Still needs work
	public static void mateParents(Chromo parent1, Chromo parent2, Chromo child1, Chromo child2)
    {
		int xoverPoint1;
		int xoverPoint2;

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover

		case 2:     //  Two Point Crossover

		case 3:     //  Uniform Crossover

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child2.rawFitness = -1;   //  Fitness not yet evaluated
	}

	//Creates a child from a single chromo from the population
	public static void mateParents(Chromo parent, Chromo child)
    {
		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
	}

	//Copies one chromosome to another
	public static void copyB2A (ChromoMap targetA, ChromoMap sourceB)
    {
		targetA.chromo = sourceB.chromo;
		targetA.rawFitness = sourceB.rawFitness;
		return;
	}
}
