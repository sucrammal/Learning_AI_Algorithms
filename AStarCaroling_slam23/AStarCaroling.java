import java.util.ArrayList;
import java.util.Scanner;
   
/**
class to practice implementing the A* algorithm
@author Marcus Lam
@version 2023/02/21
*/
public class AStarCaroling
{
   /*
   Stores the codes for locations:
   00 = Main Building
   01 = Watson
   02 = Flinn
   03 = Redlich
   04 = Edelman
   05 = Wieler
   06 = Memo
   07 = Buehler
   08 = Coy
   09 = Tinker
   10 = V-S
   11 = Garland
   12 = Dana
   */
   public static final String[] DORM_NAMES = { "Main", "Watson", "Flinn", 
      "Redlich", "Edelman", "Wieler", "Memo", "Buehler", "Coy", "Tinker", 
      "V-S", "Garland", "Dana" };
   
   /* Stores the adjacency matrix of all the dorms */
   public static final double[][] DISTANCES = {
   /*          0    1     2    3    4    5    6    7    8    9    10   11   12 */
   /* 00 */ { 0,   5.5,  -1,  4.0, 8.5, 7.0, 5.0, 5.5, 5.7, 5.2, 7.2, 9.5, 9.3 },
   /* 01 */ { 5.5, 0,    3.7, 2.2, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  11.5 },
   /* 02 */ { -1,  3.7,  0,   4.0, 3.8, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
   /* 03 */ { 4.0, 2.2,  4.0, 0,   5.5, 6.5, 7.0, -1,  -1,  -1,  -1,  -1,  -1 },
   /* 04 */ { 8.5, -1,   3.8, 5.5, 0,   5.0, -1,  -1,  -1,  -1,  -1,  -1,  -1 },
   /* 05 */ { 7.0, -1,   -1,  6.5, 5.0, 0,   4.0, -1,  -1,  -1,  -1,  -1,  -1 },
   /* 06 */ { 5.0, -1,   -1,  7.0, -1,  4.0, 0,   2.5, -1,  6.0, -1,  -1,  -1 },
   /* 07 */ { 5.5, -1,   -1,  -1,  -1,  -1,  2.5, 0,   2.2, -1,  -1,  -1,  -1 },
   /* 08 */ { 5.7, -1,   -1,  -1,  -1,  -1,  -1,  2.2, 0,   2.0, -1,  -1,  -1 },
   /* 09 */ { 5.2, -1,   -1,  -1,  -1,  -1,  6.0, -1,  2.0, 0,   3.3, -1,  9.0 },
   /* 10 */ { 7.2, -1,   -1,  -1,  -1,  -1,  -1,  -1,  -1,  3.3, 0,   3.3, 6.5 },
   /* 11 */ { 9.5, -1,   -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  3.3, 0,   4.2 },
   /* 12 */ { 9.3, 11.5, -1,  -1,  -1,  -1,  -1,  -1,  -1,  9.0, 6.5, 4.2, 0 },
   };
   
   /**
   Tracks the number of Path objects created/searched. 
   */
   public static int pathsCreated;
   
   /**
   finds the best path from start->end including all nodes
   (modified version of the TSP with modified version of A*!)
   @param start the starting node
   @param end the goal node
   @return optimal path if possible, else empty path
   */
   public Path findBestPath(int start, int end) 
   {
      ArrayList<Path> pQueue = new ArrayList<Path>();
      ArrayList<Integer> neighbors = new ArrayList<Integer>();
      pathsCreated = 1;
      Path tempPath = new Path();
      Path deepCopy = new Path();
      tempPath.path.add(start);
      pQueue.add(tempPath);
      
      while (pQueue.size() > 0) 
      {
         tempPath = pQueue.remove(0);
         if ((tempPath.path.get(tempPath.path.size()-1) == end) && (tempPath.path.size() == 13)) 
         {
            return tempPath;
         }
         
         else 
         {
            neighbors = new ArrayList<Integer>(); 
            for (int i = 0; i < 13; i++) // get all the neighbors. 
            {
               if ((!tempPath.path.contains(i))&&(DISTANCES[tempPath.path.get(tempPath.path.size() - 1)][i] > 0)) // checks if valid neighbor
               {
                  neighbors.add(i);
               }
            }
               
            for (int someNeighbor : neighbors) 
            {
               deepCopy = tempPath.clone();
               pathsCreated++;
               deepCopy.distance = (deepCopy.distance + DISTANCES[deepCopy.path.get(deepCopy.path.size() - 1)][someNeighbor]);
               deepCopy.path.add(someNeighbor);
               int index = 0;
               for (Path somePath: pQueue) 
               {
                  if (somePath.distance < deepCopy.distance) 
                  {
                     index++;
                  }
                  
               }
               
               pQueue.add(index, deepCopy);
               
            }
             
         }
         
      }
      
      return new Path();
   
   }
   
   /**
   Executes findBestPath, takes in user input. 
   */     
   public void run()
   {
      Scanner reader = new Scanner(System.in);
      
      System.out.print("Enter starting dorm: ");
      int start = Integer.parseInt(reader.nextLine());
   
      Path bestPath = findBestPath(start, 0);
      
      System.out.println("The best path from " + AStarCaroling.DORM_NAMES[start]
         + " is " + bestPath.toPrettyString());      

   }
   
   public static void main(String[] args)
   {
      AStarCaroling engine = new AStarCaroling();
      engine.run();
      System.out.println("Found solution after " + engine.pathsCreated + " paths");
   }
   
   /**
   class to store a path for A*
   */
   private class Path
   {
      /** distance traveled so far */
      public double distance;
      
      /** nodes included in this path */
      public ArrayList<Integer> path;
      
      /** 
      default constructor 
      */ 
      public Path()
      {
         distance = 0.0;
         path = new ArrayList<Integer>();
      }
      
      /**
      converts vars to a string
      @return class vars as a string
      */
      public String toString()
      {
         return "D: " + distance + ", P: " + path;
      }
      
      /**
      converts vars to a string with dorm names
      @return class vars as a string with dorm names
      */
      public String toPrettyString()
      {
         String out = "D: " + distance + ", P: ";
         
         for(int dormNum : path)
         {
            out += DORM_NAMES[dormNum] + ", ";
         }
         
         return out;
      }
      
      /**
      creates a deep copy of this path
      @return a deep copy of this path
      */
      public Path clone()
      {
         Path newPath = new Path();
         newPath.distance = this.distance;
         for(int loc : this.path)
         {
            newPath.path.add(loc);
         }
         
         return newPath;
      }
   }
}