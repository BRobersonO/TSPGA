import java.util.*;

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
                randnum = TSPMapSearch.r.nextDouble();

                for (j = 0; j < Parameters.popSize; j++)
                {
                    rWheel = rWheel + TSPMapSearch.memberMap[j].proFitness;
                    if (randnum < rWheel) return(j);
                }

                break;

			case 2:     //  Tournament Selection
				randnum = TSPMapSearch.r.nextDouble();
				k = (int) (randnum * Parameters.popSize);
				
				randnum = TSPMapSearch.r.nextDouble();
				j = (int) (randnum * Parameters.popSize);
				
				if (TSPMapSearch.memberMap[k].proFitness > TSPMapSearch.memberMap[j].proFitness) {
					return(k);
				}
				else {
					return(j);
				}

            case 3:     // Random Selection
                randnum = TSPMapSearch.r.nextDouble();
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
		CreateChild(parent1, parent2, child1);
		CreateChild(parent2, parent1, child2);

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

	private static void CreateChild(ChromoMap parent1, ChromoMap parent2, ChromoMap child)
	{
		int i, j;
		int half = parent1.chromo.size() / 2;
		ChromoMap.copyB2A(child, parent1);
		
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
			else if(i < child.chromo.size())
			{
				child.chromo.set(i, parent2.chromo.get(j)); 
				i++;
			}
			else break;
		}
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
