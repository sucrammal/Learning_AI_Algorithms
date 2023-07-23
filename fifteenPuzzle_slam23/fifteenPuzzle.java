import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;


/**
Program to solve the 15 puzzle problem game using A*
@author Marcus Lam 
@version 2023/03/02
*/
public class fifteenPuzzle
{

   /**
   Stores the original, unmodified playGrid. -1 is the empty square. 
   */
   public int[][] playGrid = {
   {0, 1, 2, 3},
   {4, 5, 6, 7},
   {8, 9, 10, 11},
   {12, 13, 14, -1}
   };
   
   /**
   A grid with a simple configuration used for debugging.  
   */
   public int[][] tempGrid = {
   {0, 1, 2, 3},
   {4, 5, 6, 7},
   {8, 9, -1 , 10},
   {11, 12, 13, 14}
   };

   
   /**
   An arbitrary integer to indicate an invalid neighbor of a tile. 
   */
   public static int INVALID_NEIGHBOR = -10;
      
   /**
   Assigns arbitrary integer to represent EMPTY tile 
   */
   public static int EMPTY = -1;
   
   /**
   Assigns 0 to represent moving the North tile.  
   */
   public static int NORTH = 0; 
   
   /**
   Assigns 1 to represent moving the East tile.  
   */
   public static int EAST = 1;
   
   /**
   Assigns 2 to represent moving the South tile.  
   */
   public static int SOUTH = 2;  
   
   /**
   Assigns integer from 3 to represent moving the West tile.  
   */
   public static int WEST = 3; 
   
   /**
   Randomize a 2D arrays's contents by swapping indexes. 
   @param some 2D array
   @return a randomized 2D array. 
   */
   public int[][] randomizedGrid(int[][] someGrid) 
   {
   
      int numRows = someGrid.length;
      int numCols = someGrid[0].length; 
      int randomIndexA1, randomIndexA2, randomIndexB1, randomIndexB2 ;
      
      for (int i = 0; i < 20; i++){
         randomIndexA1 = (int)Math.floor(Math.random() * (numRows)); // generates random index from 0-15
         randomIndexA2 = (int)Math.floor(Math.random() * (numCols));
         randomIndexB1 = (int)Math.floor(Math.random() * (numRows));
         randomIndexB2 = (int)Math.floor(Math.random() * (numCols));
         
         int tempInt = someGrid[randomIndexA1][randomIndexA2];
         someGrid[randomIndexA1][randomIndexA2] = someGrid[randomIndexB1][randomIndexB2];
         someGrid[randomIndexB1][randomIndexB2] = tempInt; 
         
      }
      
      System.out.println("Start Grid: ");
      printGrid(someGrid);
      return someGrid;       
   }
   
   /**
   Randomized a 2D arrays's contents by moving the empty tile randomly.  
   @param some 2D array
   @param how many random moves to be made. 
   @return a randomized 2D array. 
   */
   public int[][] altRandomizedGrid(int[][] someGrid, int randomizeCount)
   {
      // make randomizeCount number of random moves. 
      for (int i = 0; i < randomizeCount; i++)
      {
         int[] emptyTilePosition = findIndex(someGrid, EMPTY);
         int emptyRow = emptyTilePosition[0];
         int emptyCol = emptyTilePosition[1];
         int[] neighborList = getNeighbors(someGrid, emptyRow, emptyCol); 
         boolean moveMade = false; 
         
         while (!moveMade) 
         {
            int random = (int)Math.floor(Math.random() * (4)); // random number from 0-3 
            
            switch (random) 
            {
               case 0: //N 
                  if (neighborList[0] != INVALID_NEIGHBOR) 
                  {
                     someGrid[emptyRow][emptyCol] = someGrid[emptyRow - 1][emptyCol]; 
                     someGrid[emptyRow - 1][emptyCol] = EMPTY;
                     moveMade = true;
                  }
                  
                  break;
                  
               case 1: // E
                  if (neighborList[1] != INVALID_NEIGHBOR) 
                  {
                     someGrid[emptyRow][emptyCol] = someGrid[emptyRow][emptyCol + 1]; 
                     someGrid[emptyRow][emptyCol + 1] = EMPTY;
                     moveMade = true;
                  }
                  
                  break;
                  
               case 2: // S
                  if (neighborList[2] != INVALID_NEIGHBOR) 
                  {
                     someGrid[emptyRow][emptyCol] = someGrid[emptyRow + 1][emptyCol]; 
                     someGrid[emptyRow + 1][emptyCol] = EMPTY;
                     moveMade = true;
                  }
                  
                  break;
                  
               case 3: // W
                  if (neighborList[3] != INVALID_NEIGHBOR) 
                  {
                     someGrid[emptyRow][emptyCol] = someGrid[emptyRow][emptyCol - 1]; 
                     someGrid[emptyRow][emptyCol - 1] = EMPTY;
                     moveMade = true;
                  }
                  
                  break;
                  
               default: 
                  break;
            }
            
         }
         
      }
      
      return someGrid; 
   }
   
   /**
   Gets the neighbors of some given index of a 2D array: neighbor tiles are directly above, below, left, and right of the tile in question. 
   @param some 2D array
   @param row index of the specific tile in search 
   @param column index of the specific tile in search
   @return 1D array with the sets of valid neighbors with order: North, East, South, West.   
   */
   public int[] getNeighbors(int[][] someGrid, int rowIndex, int colIndex) 
   {
      int maxRows = someGrid.length;
      int maxCols = someGrid[0].length; 
      int[] neighborList = new int[4];
      
      for (int i = 0; i < neighborList.length; i++) 
      {
         neighborList[i] = INVALID_NEIGHBOR; 
      }
       
      if ((rowIndex - 1) >= 0) // North
      {
         neighborList[0] = 0;
      } 
      
      if ((colIndex + 1) < maxCols) // East 
      {
         neighborList[1] = 1;
      } 
      
      if ((rowIndex + 1) < maxRows) // South
      {
         neighborList[2] = 2;
      }
      
      if ((colIndex - 1) >= 0) // West 
      {
         neighborList[3] = 3;
      }
      
      return neighborList; 
   
   }
   
   /**
   Helper function to find the row and column indexes of a specific unique element of a 2D array. 
   @param some 2D array
   @param the unique element that we want to find the position of. 
   @return an array that stores the row and column index.    
   */
   public int[] findIndex(int[][] someGrid, int keyNum)
   {
      // initialising result array to -1 in case keyString is not found
      int[] position = {-1, -1};
      
      // iterate through the row elements, then the columns.  
      for (int row = 0; row < someGrid.length; row++) 
      {
         for (int col = 0; col < someGrid[row].length; col++) 
         {
            if (someGrid[row][col] == (keyNum)) 
            {
               position[0] = row;
               position[1] = col;
               return position;
            }
            
         }
         
      }
      
      // if keyNum is not found then {-1,-1} is returned
      return position;
   }
   
   /**
   Prints out the 2D array nicely, row on top of row.  
   @param some 2D array
   */
   public void printGrid(int[][] someGrid)
   {
      System.out.println("");
      // Loop through all rows
      for (int row = 0; row < someGrid.length; row++)
      {
         System.out.println(Arrays.toString(someGrid[row]));      
      }
        
   }
   
   /**
   Print function used for debugging: based on then numeric move made, print corresponding statement.   
   @param some integer from 0-3
   */
   public void printMove(int move) 
   {
      switch (move) 
      {
         case 0: 
            System.out.println("Moved North Tile");
            break;
         case 1: 
            System.out.println("Moved East Tile");
            break;
         case 2: 
            System.out.println("Moved South Tile");
            break;
         case 3: 
            System.out.println("Moved West Tile");
            break;
      } 
      
   }
   
   /**
   Compress the play grid into a string for easy comparison between different grids.    
   @param some 2D play grid. 
   @return a string with the compressed grid. 
   */
   public String compressGrid(int[][] someGrid) 
   {
      String compressedStr = "";
      for (int row = 0; row < someGrid.length; row++) 
      {
         for (int col = 0; col < someGrid[row].length; col++) 
         {
            compressedStr = compressedStr + Integer.toString(someGrid[row][col]);           
         }
         
      }
      
      return compressedStr; 
   }
   
      
   /**
   Solves the 15 puzzle problem using A* 
   @param some 2D array
   @return Path, the solution to the 15 puzzle problem  
   */
   public Path findSolution(int[][] someGrid)
   {
      ArrayList<Path> pQueue = new ArrayList<Path>(); 
      ArrayList<String> prevGridStates = new ArrayList<String>();
      
      Path tempPath = new Path();
      Path deepCopy = new Path();
      int[] neighborList = new int[4];
      
      tempPath.currentBoard = altRandomizedGrid(someGrid, 10); //altRandomizedGrid(playGrid);
      prevGridStates.add(compressGrid(tempPath.currentBoard));
      pQueue.add(tempPath);
      
      int[] emptyTilePosition = new int[2];
      int emptyRow = -1;
      int emptyCol = -1;
      
      System.out.println("Initial Grid: ");
      printGrid(tempPath.currentBoard);
      System.out.println("");
      
      // now, since pQueue has a first move, start the search. 
      while (pQueue.size() > 0) 
      {
         tempPath = pQueue.remove(0);
         tempPath.computeScore();
         System.out.println("Top priority Score: " + tempPath.score);
         prevGridStates.add(compressGrid(tempPath.currentBoard)); // update catalog of saved previous game states. 
         
         // Check for the end state. 
         if (compressGrid(tempPath.currentBoard).equals("01234567891011121314-1"))
         {
            return tempPath; 
         }
         
         else 
         {
            neighborList = new int[4];
            emptyTilePosition = findIndex(tempPath.currentBoard, EMPTY);
            emptyRow = emptyTilePosition[0];
            emptyCol = emptyTilePosition[1];
            neighborList = getNeighbors(tempPath.currentBoard, emptyRow, emptyCol); // stores the neighboring tiles using their assigned number. 
            
            for (int neighbor: neighborList) {
               deepCopy = tempPath.clone();
               if ((neighbor != INVALID_NEIGHBOR)&&(!(deepCopy.isRedundantMove(neighbor))))
               {
                  deepCopy.moveBoard(neighbor, emptyRow, emptyCol);
                  deepCopy.computeScore();
                  
                  // Check if the game state is same as some state before.
                  int check = 0;  
                                     
                  for (String state: prevGridStates)
                  {
                     check = 0; 
                     if (compressGrid(deepCopy.currentBoard).equals(state)) 
                     {
                        check++; 
                     }
                  }
                  
                  if (check == 0) // if not repeating a game state,
                  {
                  // Now, insert the new Path object into the correct index based on score. 
                     int index = 0;
                     for (Path somePath: pQueue) 
                     {
                        if (somePath.score < deepCopy.score) 
                        {
                           index++;
                        }
                        
                        else 
                        {
                           break; 
                        }
                     
                     }
                  
                     pQueue.add(index, deepCopy);
                     prevGridStates.add(compressGrid(deepCopy.currentBoard));
                     System.out.println(deepCopy.score); 
                  }
               }
               
            }
            
         }
      
      }
            
      return new Path(); 
   }
   
   
   /**
   Runs the agent to solve the 15 puzzle problem. 
   */
   public void run()
   {
      Scanner reader = new Scanner(System.in);
      Path solution = findSolution(playGrid);
      printGrid(solution.currentBoard);
      System.out.println(solution.path);
      
   }
   
   public static void main(String[] args)
   {
      fifteenPuzzle engine = new fifteenPuzzle();
      engine.run();
   }
   
   
   /**
   class to store a path for A*: includes score and solution.  
   */
   private class Path
   {
      /** score for how arranged or solved the board is so far */
      public double score;
      
      /** nodes included in this path */
      public ArrayList<Character> path;
      
      /** Stores the configuration of the play grid. */
      public int[][] currentBoard;
      
      /** 
      default constructor 
      */ 
      public Path()
      {
         score = 0;
         path = new ArrayList<Character>();
         currentBoard = new int[4][4]; 
      }
      
      /**
      Evaluates the current board to determine a score based on the distances between 
      the ideal and actual location of each tile.   
      */
      public void computeScore()
      {
         score = 0.0;
         
         int rowDistance = 0;
         int colDistance = 0;
         
         for (int row = 0; row < currentBoard.length; row++)
         {
            for (int col = 0; col < currentBoard[0].length; col++)
            {
               if (currentBoard[row][col] != EMPTY)
               {
                  int[] actualTileLocation = findIndex(currentBoard, ((row * currentBoard[0].length) + col));
                  int actualTileRow = actualTileLocation[0];
                  int actualTileCol = actualTileLocation[1];
               
                  // calculate city block distance between supposed and actual location of the tile. 
                  rowDistance = rowDistance + Math.abs(row-actualTileRow);
                  colDistance = colDistance + Math.abs(col-actualTileCol);
                  
               }
            
            }
                        
         }
         
         // Separate case for the empty tile. 
         
         int[] emptyTilePosition = findIndex(currentBoard, EMPTY);
         int emptyRow = emptyTilePosition[0];
         int emptyCol = emptyTilePosition[1];
         
         rowDistance = rowDistance + Math.abs((currentBoard.length - 1) - emptyRow);
         colDistance = colDistance + Math.abs((currentBoard[0].length - 1) - emptyCol);
         
         // Bigger the distance, smaller the score. Add a 1.0 to the denominator to prevent divide by 0. 
         score = rowDistance + colDistance; 
                
      }
      
      /** 
      Makes an adjacent tile to the -1 empty square based on the selected move, assuming the move is a valid neighbor.  
      @param int from 0-3 corresponding to some adjacent tile. N, E, S, W.  
      @param the row of the empty tile
      @param the column of the empty tile. 
      */
      public void moveBoard(int someMove, int emptyRow, int emptyCol) 
      {
      
         switch (someMove) 
         {
            case 0: // N
               path.add('N');
               currentBoard[emptyRow][emptyCol] = currentBoard[emptyRow - 1][emptyCol]; 
               currentBoard[emptyRow - 1][emptyCol] = EMPTY;
               break; 
            case 1: // E
               path.add('E');
               currentBoard[emptyRow][emptyCol] = currentBoard[emptyRow][emptyCol + 1]; 
               currentBoard[emptyRow][emptyCol + 1] = EMPTY;
               break;
            case 2: // S
               path.add('S');
               currentBoard[emptyRow][emptyCol] = currentBoard[emptyRow + 1][emptyCol]; 
               currentBoard[emptyRow + 1][emptyCol] = EMPTY;
               break;
            case 3: // W
               path.add('W');
               currentBoard[emptyRow][emptyCol] = currentBoard[emptyRow][emptyCol - 1]; 
               currentBoard[emptyRow][emptyCol - 1] = EMPTY;
               break;
            default: 
               break; 
         }
         
      }
      
      /**
      Compares new potential move to the latest move made to see if they are redudant.
      @param potential new move
      @return true or false, true if redundant. 
      */
      public boolean isRedundantMove(int someMove) 
      {
         if (path.size() > 0)
         {
            if (((path.get(path.size() - 1)).equals('N'))&&(someMove == 2)) //N and S
            {
               return true; 
            } 
            
            else if (((path.get(path.size() - 1)).equals('E'))&&(someMove == 3)) // E and W
            {
               return true; 
            }
            
            else if (((path.get(path.size() - 1)).equals('S'))&&(someMove == 0)) // S and N
            {
               return true; 
            }
            
            else if (((path.get(path.size() - 1)).equals('W'))&&(someMove == 1)) // W and E
            {
               return true; 
            }
            
         }
         
         else 
         {
            return false; 
         }
         
         return false;
      }
      
      /**
      converts vars to a string
      @return class vars as a string
      */
      public String toString()
      {
         return "S: " + score + ", P: " + path;
      }
         
      /**
      creates a deep copy of this path
      @return a deep copy of this path
      */
      public Path clone()
      {
         Path newPath = new Path();
         newPath.score = this.score;
         for (int row = 0; row < currentBoard.length; row++)
         {
            for (int col = 0; col < currentBoard[0].length; col++)
            {
               newPath.currentBoard[row][col] = this.currentBoard[row][col]; 
            }
            
         }
         
         for (char move : this.path)
         {
            newPath.path.add(move);
         }
         
         return newPath;
      }
      
   }
   
}