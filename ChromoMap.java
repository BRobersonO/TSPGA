import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChromoMap
{
	public List<Integer> chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

    private static double randnum;

    public ChromoMap()
    {
		//Sets the initial chromo list with the keys from the citiesMap
        chromo = new ArrayList<Integer>(TSPMap.citiesMap.keySet());

        //Shuffles the chromo list to randomize the values
        Collections.shuffle(chromo);

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}

    
	//Mutate a Chromosome Based on Mutation Type - Still needs work
	public void doMutation()
    {
		switch (Parameters.mutationType)
		{
			case 1:     //  Replace with new random number
				Random rand = new Random();
				//System.out.println(this.chromo.size());
				int x = rand.nextInt(this.chromo.size());
				
				int temp = this.chromo.get(x);
				
				int next = (x + 1) % this.chromo.size();
				this.chromo.set(x, this.chromo.get(next));
				this.chromo.set(next, temp);
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
                    rWheel = rWheel + Search.memberMap[j].proFitness;
                    if (randnum < rWheel) return(j);
                }

                break;

			case 2:     //  Tournament Selection
				randnum = Search.r.nextDouble();
				k = (int) (randnum * Parameters.popSize);
				
				randnum = Search.r.nextDouble();
				j = (int) (randnum * Parameters.popSize);
				
				if (Search.memberMap[k].rawFitness > Search.memberMap[j].rawFitness) {
					return(k);
				}
				else {
					return(j);
				}

            case 3:     // Random Selection
                randnum = Search.r.nextDouble();
                j = (int) (randnum * Parameters.popSize);

                return(j);

            default:
                System.out.println("ERROR - No selection method selected");
		}

	    return(-1);
	}

	//Creates a new child from two selected chromos from the population - Still needs work
	public static void mateParents(ChromoMap parent1, ChromoMap parent2, ChromoMap child1, ChromoMap child2)
    {
		int i, j;

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover

		case 2:     //  Two Point Crossover

		case 3:     //  Uniform Crossover
			int half = parent1.chromo.size() / 2;
			ChromoMap.copyB2A(child1, parent1);
			
			List<Integer> compareTo = new ArrayList<Integer>(half);

			for(i = 0; i < half; i++) 
			{
				compareTo.add(i);
			}

			for(j = 0; j < parent2.chromo.size(); j++) 
			{
				if(compareTo.contains(parent2.chromo.get(j))) 
				{
					continue;
				}
				else if(i < child1.chromo.size())
				{
					child1.chromo.set(i, parent2.chromo.get(j)); 
					i++;
				}
				else break;
			}
			break;

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child1.sclFitness = -1;   //  Fitness not yet scaled
		child1.proFitness = -1;   //  Fitness not yet proportionalized
		child2.rawFitness = -1;   //  Fitness not yet evaluated
		child2.sclFitness = -1;   //  Fitness not yet scaled
		child2.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//Creates a child from a single chromo from the population
	public static void mateParents(ChromoMap parent, ChromoMap child)
    {
		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//Copies one chromosome to another
	public static void copyB2A (ChromoMap targetA, ChromoMap sourceB)
    {
		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}
}
