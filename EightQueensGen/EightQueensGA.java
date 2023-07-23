import java.util.Scanner;
import java.util.Arrays;

/**
solves eight queens problem using genetic algorithms
@author Marcus Lam
@version 3/23/23
*/
public class EightQueensGA
{
   public static final int BOARD_SIZE = 8;
   public static final int NUM_CHROMS = 6;
   public static final int NUM_SIMS = 10000;
   public static final int NUM_PARENTS = 2;
   public static final int NUM_CHILDREN = 4;
   public static final int MUTATE_COUNT = 3;
   
   // YOUR VARIABLES HERE
   
   public EightQueensGA()
   {            
   }
   
   /**
   runs the simulation
   */
   public void run()
   {      
      EightQueensChrom test = new EightQueensChrom();
      EightQueensChrom winner = test.run_ga(NUM_CHROMS, NUM_SIMS);
            
   }
   
   
      
   public static void main(String[] args)
   {
      EightQueensGA test = new EightQueensGA();
      test.run();
   }

   
   private class EightQueensChrom
   {
      public int[] genes;
      
      public EightQueensChrom[] myChroms; 
      
      public EightQueensChrom()
      {
         genes = new int[BOARD_SIZE];
      }
      
      public EightQueensChrom(int[] geneVals)
      {
         this();
         for(int idx = 0; idx < genes.length; idx++)
         {
            genes[idx] = geneVals[idx];
         }
      }
            
      /**
      gets fitness score of chromosome
      @return number of queens each queen attacks
      */
      public double getFitness()
      {
         int tempScore = 0;
         
         // Check if any of the Queens are in the same row. 
         for (int targetIdx = 0; targetIdx < genes.length; targetIdx++)
         {
            for (int idx = 0; idx < genes.length; idx++) 
            {
               if ((targetIdx != idx)&&(genes[targetIdx] == genes[idx])) // make sure we are not comparing the target queen to itself. 
               {
                  tempScore++; 
               }
               
            }
            
         }
         
         // Now check if any of the Queens are in the same diagonal  
         for (int targetIdx = 0; targetIdx < genes.length; targetIdx++)
         {
            for (int idx = 0; idx < genes.length; idx++) 
            {
               int targetQueenRow = genes[targetIdx]; 
               int targetQueenCol = targetIdx;
               int otherQueenRow = genes[idx];
               int otherQueenCol = idx; 
               
               if ((targetIdx != idx)&&(Math.abs(otherQueenRow - targetQueenRow) == Math.abs(otherQueenCol - targetQueenCol))) // make sure we are not comparing the target queen to itself. 
               {
                  tempScore++; 
               }
               
            }
         
         }
         
         if (tempScore == 0.0)
         {
            //System.out.println("SOLUTION FOUND: "); 
         }
         
         return tempScore;
      }
      
      /**
      Generate the initial population of Chromosones 
      @param the total number of chromosomes in a population.
      */
      public void generate_initial_population (int num_chroms)
      {
         myChroms = new EightQueensChrom[num_chroms]; 
         
         for (int idx = 0; idx < myChroms.length; idx++) 
         {
            EightQueensChrom newChrom = new EightQueensChrom();
            
            for (int chromIdx = 0; chromIdx < genes.length; chromIdx++)
            {
               int tempRandom = (int)Math.floor(Math.random() * (BOARD_SIZE)); // generate integer from 0-8
               newChrom.genes[chromIdx] = tempRandom; 
            }
            
            myChroms[idx] = newChrom; 
         }
         
      }
      
      /**
      Creates a list of probabilities of parent selection for the given population. 
      @return list of doubles containing the probability of each chromosone.
      */
      public double[] set_probabilities_of_population()
      {
         double totalFitness = 0; 
         for (EightQueensChrom someChrom: myChroms) 
         {
            totalFitness += (1.0 / someChrom.getFitness()); // take the reciprocal of the fitness. Lower fitness, the better.
         }
         
         double[] probabilitiesList = new double[NUM_CHROMS];
         
         for (int i = 0; i < myChroms.length; i++) 
         {
            probabilitiesList[i] = ((1.0 / myChroms[i].getFitness()) / totalFitness);
         }
         
         return probabilitiesList; 
         
      }
   
      
      /**
      Selects the parents to create the next generation. 
      @param number of parents to select
      @return list of chosen parent chromosones.  
      */
      public EightQueensChrom[] roulette_wheel_selection(int number_of_selections)
      {
         EightQueensChrom[] parentList = new EightQueensChrom[number_of_selections];
         double[] probabilitiesList = set_probabilities_of_population();
         
         for (int parent = 0; parent < number_of_selections; parent++)
         {
            double rouletteProbability = Math.random(); // gives a number from 0 to 1, inclusive. 
            int idx = 0; 
         
            while (rouletteProbability > probabilitiesList[idx])
            {
               rouletteProbability -= probabilitiesList[idx];
               idx++; 
            }
            
            parentList[parent] =  myChroms[idx];
         
         }
         
         return parentList;  
      }
      
      
      /**
      Create the new population of offspring from selected parents, using one-point crossover
      @param the array of selected parents
      @param number of offspring to produce
      @return list of offspring
      */
      public EightQueensChrom[] reproduce_children(EightQueensChrom[] the_chosen, int num_children)
      {
         int chosenParent1 = (int)(Math.random()*(the_chosen.length)); // Random index from 0 to last index of the_chosen
         int chosenParent2 = chosenParent1; 
         
         while (chosenParent1 == chosenParent2)
         {
            chosenParent2 = (int)(Math.random()*(the_chosen.length)); // make sure both indexes are different. 
         }
         
         EightQueensChrom parent1 = the_chosen[chosenParent1];
         EightQueensChrom parent2 = the_chosen[chosenParent2];
               
         EightQueensChrom[] childrenList = new EightQueensChrom[num_children];
         
         for (int i = 0; i < childrenList.length; i++)
         {
            int crossoverIndex = (int)(Math.random()*(BOARD_SIZE));
            EightQueensChrom newChild = new EightQueensChrom(); 
            
            for (int idx = 0; idx <= crossoverIndex; idx++)
            {
               newChild.genes[idx] = parent1.genes[idx]; 
            }
            
            for (int nextIdx = crossoverIndex; nextIdx < BOARD_SIZE; nextIdx++) 
            {
               newChild.genes[nextIdx] = parent2.genes[nextIdx];
            } 
            
            childrenList[i] = newChild; 
         }
         
         return childrenList; 
      }
      
      /**
      Create the new population of offspring from selected parents, using two-point crossover
      @param the array of selected parents
      @param number of offspring to produce
      @return list of offspring
      */
      public EightQueensChrom[] reproduce_children_two(EightQueensChrom[] the_chosen, int num_children)
      {
         int chosenParent1 = (int)(Math.random()*(the_chosen.length)); // Random index from 0 to last index of the_chosen
         int chosenParent2 = chosenParent1; 
         
         while (chosenParent1 == chosenParent2)
         {
            chosenParent2 = (int)(Math.random()*(the_chosen.length)); // make sure both indexes are different. 
         }
         
         EightQueensChrom parent1 = the_chosen[chosenParent1];
         EightQueensChrom parent2 = the_chosen[chosenParent2];
         
         EightQueensChrom[] childrenList = new EightQueensChrom[num_children];
         
         for (int i = 0; i < childrenList.length; i++)
         {
            int crossoverIndex1 = (int)(Math.random()*(BOARD_SIZE-1)); // index 1 goes from 0 to BOARD_SIZE-2, which excludes the last index. 
            int crossoverIndex2 = crossoverIndex1; 
            while ((crossoverIndex1 >= crossoverIndex2))
            {
               crossoverIndex2 = (int)(Math.random()*(BOARD_SIZE)); // make sure index 1 < index 2 
            } 
         
            EightQueensChrom newChild = new EightQueensChrom(); 
            
            
            for (int idx = 0; idx <= crossoverIndex1; idx++)
            {
               newChild.genes[idx] = parent1.genes[idx]; 
            }
            
            for (int nextIdx = crossoverIndex1; nextIdx < crossoverIndex2; nextIdx++) 
            {
               newChild.genes[nextIdx] = parent2.genes[nextIdx];
            } 
            
            for (int finalIdx = crossoverIndex2; finalIdx < BOARD_SIZE; finalIdx++)
            {
               newChild.genes[finalIdx] = parent2.genes[finalIdx];
            }
            
            childrenList[i] = newChild; 
         }
         
         return childrenList; 
      
      }
      
      
      /**
      mutates each offspring using single-point mutation (MUTATE_COUNT times)
      @param the_children - the array of children to mutate
      @param mutateCount - the number of times to mutate each chromosome. 
      @return the array of mutated children
      */
      public EightQueensChrom[] mutate_children(EightQueensChrom[] the_children, int mutateCount)
      {
         for (EightQueensChrom someChild: the_children)
         {
            for (int i = 0; i < mutateCount; i++)
            {
               int randomIndex = (int)(Math.random()*(BOARD_SIZE-1));
               someChild.genes[randomIndex] = (int)(Math.random()*(BOARD_SIZE));
            }
         
         }
                  
         return the_children; 
      }
      
      
      /**
      Adds given children to the population by removing the weakest population members
      @param the_children - the array of children to mutate
      */
      public void merge_population_and_children(EightQueensChrom[] the_children)
      {
         sortChroms();
         int childIndex = 0;
         
         for (int i = myChroms.length-1; i > (myChroms.length - 1 - NUM_CHILDREN); i--)
         {
            myChroms[i] = the_children[childIndex];
            childIndex++; 
         }
         
      }
      
      /**
      sorts myChroms in increasing order based on getFitness() values
      */
      public void sortChroms()
      {
         boolean isSorted = false; 
         while (!isSorted)
         {
            isSorted = true;
            for (int j = 0; j < myChroms.length - 1; j++) // j are the other indexes that the i chrom is compared to. 
            {
               // swap myChroms[j+1] and myChroms[j]; Sort Chroms from best to worst 
               if (myChroms[j].getFitness() > myChroms[j + 1].getFitness()) { 
                  EightQueensChrom temp = myChroms[j]; // Create temporary copy to swap values. 
                  myChroms[j] = myChroms[j + 1];
                  myChroms[j + 1] = temp;
                  isSorted = false; 
               }
               
            }
            
         }
         
      }
      
      
      /**
      Runs the GA
      @param population_size - number of chromosomes in population
      @param number_of_generations - number of generations to simulate
      @return the fittest chromosome
      */
      public EightQueensChrom run_ga(int population_size, int number_of_generations)
      {
         int iterateCount = 0;
         for (int i = 0; i < 10; i++)
         {
            generate_initial_population(NUM_CHROMS);
            
            for (int j = 0; j < (number_of_generations / 10); j++)
            { 
            
               EightQueensChrom[] parentList = roulette_wheel_selection(NUM_PARENTS);
            
               EightQueensChrom[] newPopulation = reproduce_children_two(parentList, NUM_CHILDREN); 
                       
               mutate_children(newPopulation, MUTATE_COUNT);
               
               sortChroms();
                                              
               merge_population_and_children(newPopulation);            
               
               sortChroms();
               
               for (EightQueensChrom someChrom: myChroms)
               {
                  if (someChrom.getFitness() == 0.0)
                  {
                     System.out.println("WINNER: ");
                     System.out.println(Arrays.toString(someChrom.genes)); 
                     System.out.println(someChrom.getFitness()); 
                  
                     return someChrom;
                  }
               }
               
            }
            
            iterateCount++;
            System.out.println("GENERATION " + (number_of_generations / 10)*iterateCount);
            for (EightQueensChrom someChrom: myChroms)
            {
               System.out.println(Arrays.toString(someChrom.genes) + " / Fitness: " + someChrom.getFitness());
            }
            
            System.out.println("Press \"ENTER\" to continue...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            
         }
         
         System.out.println("Fittest of Last Gen: ");
         System.out.println(Arrays.toString(myChroms[0].genes)); 
         System.out.println(myChroms[0].getFitness()); 
                 
         return myChroms[0]; 
      }
   
      
      /**
      gets string representation of chromosome
      @return genes as a string
      */
      public String toString()
      {
         String out = "";
         
         for(int idx = 0; idx < genes.length; idx++)
         {
            out += genes[idx];
         }
         return out;
      }
      
   }
}