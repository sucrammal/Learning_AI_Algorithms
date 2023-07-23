import java.util.Scanner;
import java.util.Arrays;

/**
solves the factory assignment problem using genetic algorithms
@author Marcus Lam
@version 05/15/2023
*/

public class FactoryAssignment
{
   /* The total available locations; also the number of factories */
   public static final int LOCATION_COUNT = 8;
   
   /* size of chromosome population */
   public static final int NUM_CHROMS = 6;
   
   /* Number of simulations to evolve the chromosomes */
   public static final int NUM_SIMS = 1000;
   
   /* Number of parents to select out of each population of chromosomes */
   public static final int NUM_PARENTS = 2;
   
   /* Number of children to replace the worst performing few in the prev. population  */
   public static final int NUM_CHILDREN = NUM_CHROMS - NUM_PARENTS;
   
   /* How many times to mutate each chromosome while reproducing offspring */
   public static final int MUTATE_COUNT = 2;
   
   /* Approximately scales down the flow rate fitness to the scale of time fitness
      Time fitness values are around 25, while flow rate fitness values are around 82000, 
      just because flow rate involves multiplication, and is much larger */
   public static final double TIME_PRIORITY = 0.0002; 
   
   /* number of times to randomize the initial generation of chromosomes */
   public static final int RANDOMIZER = 20;
   
   /* Distance matrix between locations */
   int[][] distanceMatrix = {
      {0, 54, 77, 69, 58, 27, 50, 19, 30}, 
      {54, 0, 68, 30, 19, 50, 73, 35, 69},
      {77, 68, 0, 99, 87, 50, 27, 81, 46},
      {69, 30, 99, 0, 11, 65, 87, 50, 84},
      {58, 19, 87, 11, 0, 53, 76, 38, 73},
      {27, 50, 50, 65, 53, 0, 22, 30, 19},
      {50, 73, 27, 87, 76, 22, 0, 53, 19},
      {19, 35, 81, 50, 38, 30, 53, 0, 34},
      {30, 69, 46, 84, 73, 19, 19, 34, 0}
      }; 
      
   /* 
   Matrix that stores the time efficiency of each factory assigned at each location. 
   Rows represent the factory, columns represent the location, index value indicates time efficiency.
   Bigger index value, worse effiency. 
   */
   int[][] timeMatrix = {
      {1, 8, 3, 10, 4, 5, 9, 5, 5},
      {4, 1, 2, 7, 11, 2, 8, 7, 12},
      {4, 12, 1, 6, 10, 2, 3, 7, 10},
      {12, 9, 8, 1, 4, 8, 3, 7, 3},
      {11, 5, 6, 9, 1, 5, 9, 5, 12},
      {6, 1, 5, 6, 3, 5, 12, 7, 3},
      {1, 9, 6, 4, 1, 9, 5, 9, 10},
      {1, 12, 9, 1, 7, 9, 12, 7, 1},
      {8, 7, 4, 12, 6, 5, 1, 2, 5}
      }; 
      
   /* 
   Flow Rate Matrix; the rate in which people travel (daily) from a certain factory to another.
   Row indicates the original location, column indicates the destination.   
   */
   int[][] flowRate = {
      {0, 43, 14, 40, 41, 27, 21, 18, 36},
      {18, 0, 32, 46, 14, 21, 26, 35, 37},
      {44, 19, 0, 13, 41, 33, 25, 18, 15},
      {16, 22, 25, 0, 20, 41, 29, 32, 43},
      {49, 34, 30, 17, 0, 23, 39, 39, 39},
      {27, 41, 42, 30, 29, 0, 41, 28, 42},
      {27, 45, 15, 31, 14, 45, 0, 18, 40},
      {20, 44, 37, 27, 22, 49, 24, 0, 40},
      {44, 28, 23, 49, 39, 21, 32, 14, 0}
      };


   /**
   Runs the GA to solve the assignment. 
   */
   public void run()
   {      
      FactoryChrom test = new FactoryChrom(); 
      test.run_ga(NUM_CHROMS, NUM_SIMS);
   }
   
   public static void main(String[] args)
   {
      FactoryAssignment test = new FactoryAssignment();
      test.run();
   }
   
   /**
   Each index in a chromosome represents a certain location, and the value of each index is the specific factory allocated to that location. 
   */
   private class FactoryChrom
   {
      public int[] genes;
      
      public FactoryChrom[] myChroms; 
      
      /**
      Basic initializer
      */
      public FactoryChrom()
      {
         genes = new int[LOCATION_COUNT];
      }
      
      /**
      Second initializer that allows to "upload" pre-existing gene. 
      @param geneVals – the values of each gene in the chosen chromosome. 
      */
      public FactoryChrom(int[] geneVals)
      {
         this();
         for(int idx = 0; idx < genes.length; idx++)
         {
            genes[idx] = geneVals[idx];
         }
         
      }
      
      /**
      Generate the initial population of Chromosones 
      @param the total number of chromosomes in a population.
      @param how many times to randomize the original uniform chromosome
      */
      public void generate_initial_population (int num_chroms, int randomizer)
      {
         myChroms = new FactoryChrom[num_chroms];
         for (int idx = 0; idx < myChroms.length; idx++) 
         {
            FactoryChrom newChrom = new FactoryChrom();
            
            // the original, uniform chromosome. 
            for (int chromIdx = 0; chromIdx < genes.length; chromIdx++)
            {
               newChrom.genes[chromIdx] = chromIdx; 
            }
            
            
            for (int i = 0; i < randomizer; i++)
            {
               int randomIndex1 = (int)Math.floor(Math.random() * (LOCATION_COUNT)); // generates random index from 0-LOCATION_COUNT
               int randomIndex2 = (int)Math.floor(Math.random() * (LOCATION_COUNT));
               
               int tempInt = newChrom.genes[randomIndex1];
               newChrom.genes[randomIndex1] = newChrom.genes[randomIndex2];
               newChrom.genes[randomIndex2] = tempInt; 
            
            }
            
            myChroms[idx] = newChrom; 
         }
      
      }
      
      /**
      gets fitness of the flowRate of a chromosome; higher the fitness, the worse. 
      @return fitness score of flowRates
      */
      public double getFlowRateFitness()
      {
         int flowRateEfficiency = 0; 
          
         // selects a location (index) where a factory is situated (value at the index). Gets distance between locations and flow rates between factories. 
         for (int targetIdx = 0; targetIdx < genes.length; targetIdx++)
         {
            for (int idx = 0; idx < genes.length; idx++) 
            {
               if (targetIdx != idx) // make sure we are not comparing the target location to itself. 
               {
                  flowRateEfficiency += distanceMatrix[targetIdx][idx] * flowRate[genes[targetIdx]][genes[idx]];
               }
               
            }
            
         }
         
         return flowRateEfficiency;
      
      }
      
      /**
      gets fitness of the time efficiency of a chromosome; higher the fitness, the worse. 
      @return fitness score of time efficiency
      */
      public double getTimeFitness()
      {
         int timeEfficiency = 0; 
         
         for (int i = 0; i < genes.length; i++)
         {
            timeEfficiency += timeMatrix[i][genes[i]]; 
         }
         
         return timeEfficiency; 
         
      }   
      
      /**
      gets overall scaled fitness score of a chromosome; higher the fitness, the worse. 
      @return fitness score
      */
      public double getFitness()
      {
         double scaledFlowRateFitness = getFlowRateFitness();
         double scaledTimeFitness = getTimeFitness(); 
         double overallFitness = scaledTimeFitness + (TIME_PRIORITY * scaledFlowRateFitness); 
         return overallFitness;
      }
      
      /**
      Creates a list of probabilities of parent selection for the given population. 
      @return list of doubles containing the probability of each chromosome
      */
      public double[] set_probabilities_of_population()
      {
         double totalFitness = 0; 
         for (FactoryChrom someChrom: myChroms) 
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
      public FactoryChrom[] roulette_wheel_selection(int number_of_selections)
      {
         FactoryChrom[] parentList = new FactoryChrom[number_of_selections];
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
      Create the new population of offspring from selected parents, using swapping random indexes 
      @param the array of selected parents
      @param number of offspring to produce
      @param how many index swaps to do per child. 
      @return list of offspring
      */
      public FactoryChrom[] reproduce_children(FactoryChrom[] the_chosen, int num_children, int mutate_count)
      {
         FactoryChrom[] childrenList = new FactoryChrom[num_children];
         
         // split the group of children into 2 random groups. Parent 1 will produce the children of the first half, Parent 2 the other. 
         int randomSplit = (int)(Math.random() * (num_children));
         int firstHalf = (int)(randomSplit * num_children); 
         int secondHalf = num_children - firstHalf; 
         
         FactoryChrom parent1 = the_chosen[0];
         FactoryChrom parent2 = the_chosen[1];
      
         // First Half
         for (int i = 0; i < randomSplit; i++)
         {
            FactoryChrom newChild = new FactoryChrom(); 
            
            // make hard copy of parent1
            for (int j = 0; j < newChild.genes.length; j++)
            {
               newChild.genes[j] = parent1.genes[j];
            }
            
            // swap indexes mutate_count times. 
            for (int idx = 0; idx < mutate_count; idx++)
            {
               int randomIndex1 = (int)(Math.random()*(LOCATION_COUNT - 1));
               int randomIndex2 = randomIndex1;
            
               while (randomIndex1 == randomIndex2)
               {
                  randomIndex2 = (int)(Math.random()*(LOCATION_COUNT - 1));
               }
            
               int tempValue1 = newChild.genes[randomIndex1];
               newChild.genes[randomIndex1] = newChild.genes[randomIndex2];
               newChild.genes[randomIndex2] = tempValue1;
            }
            
            childrenList[i] = newChild;
            
         }
         
         // Second Half
         for (int k = randomSplit; k < num_children; k++)
         {
            FactoryChrom newChild = new FactoryChrom(); 
            
            // make hard copy of parent2
            for (int j = 0; j < newChild.genes.length; j++)
            {
               newChild.genes[j] = parent2.genes[j];
            }
            
            // swap indexes mutate_count times. 
            for (int idx = 0; idx < mutate_count; idx++)
            {
               int randomIndex1 = (int)(Math.random()*(LOCATION_COUNT - 1));
               int randomIndex2 = randomIndex1;
            
               while (randomIndex1 == randomIndex2)
               {
                  randomIndex2 = (int)(Math.random()*(LOCATION_COUNT - 1));
               }
            
               int tempValue1 = newChild.genes[randomIndex1];
               newChild.genes[randomIndex1] = newChild.genes[randomIndex2];
               newChild.genes[randomIndex2] = tempValue1;
            }
            
            childrenList[k] = newChild;
            
         }
                 
         return childrenList;       
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
                  FactoryChrom temp = myChroms[j]; // Create temporary copy to swap values. 
                  myChroms[j] = myChroms[j + 1];
                  myChroms[j + 1] = temp;
                  isSorted = false; 
               }
               
            }
            
         }
         
      }
   
      
      /**
      Adds given children to the population by removing the weakest population members
      @param the_children - the array of children to mutate
      */
      public void merge_population_and_children(FactoryChrom[] the_children)
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
      Runs the GA
      @param population_size - number of chromosomes in population
      @param number_of_generations - number of generations to simulate
      @return the fittest chromosome
      */
      public FactoryChrom run_ga(int population_size, int number_of_generations)
      {
         int iterateCount = 0;
         FactoryChrom bestChrom = new FactoryChrom();
         
         for (int i = 0; i < 10; i++)
         {
            generate_initial_population(NUM_CHROMS, RANDOMIZER);
            
            for (int j = 0; j < (number_of_generations / 10); j++)
            { 
               FactoryChrom[] parentList = roulette_wheel_selection(NUM_PARENTS);
            
               FactoryChrom[] newPopulation = reproduce_children(parentList, NUM_CHILDREN, MUTATE_COUNT); 
                                      
               sortChroms();
                                              
               merge_population_and_children(newPopulation);            
               
               sortChroms(); 
               
               if ((bestChrom.getFitness() > myChroms[0].getFitness())||((i == 0)&&(j == 0)))
               {
                  bestChrom = myChroms[0];
               }
                             
            }
            
            iterateCount++;
            System.out.println("GENERATION " + (number_of_generations / 10) * iterateCount);
            for (FactoryChrom someChrom: myChroms)
            {
               System.out.println(Arrays.toString(someChrom.genes) + " / Fitness: " + someChrom.getFitness());
            }
            
            System.out.println("Press \"ENTER\" to continue...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            
         }
         
         System.out.println("Fittest of All Gens: ");
         System.out.println(Arrays.toString(bestChrom.genes)); 
         System.out.println(bestChrom.getFitness()); 
                 
         return bestChrom; 
      
      }
   
   }

}
