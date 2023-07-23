import java.util.ArrayList;
import java.util.Scanner;

/**
solves word ladders using breadth-first search
@author Marcus Lam
@version 2022/01/26
*/
public class BFSLadder
{
   /** Our instance of Dictionary. */
   private Dictionary myWordList;
   
   /** Keeps track of the words searched. */
   private int wordsVisited = 1;
   
   public BFSLadder()
   {
      myWordList = new Dictionary();
   
   }
   
   /**
   finds a word ladder connecting these two words
   startWord the starting word
   endWord the ending word
   returns the list of words in the ladder from startWord->endWord
   (or an empty list if none exists)
   */
   public ArrayList<String> getLadder(String startWord, String endWord)
   {
      ArrayList<ArrayList<String>> masterQueue = new ArrayList<ArrayList<String>>(); // This is the main queue. 
      ArrayList<String> neighbors = new ArrayList<String>(); // Contains all the neighbors of the current search word.
      ArrayList<String> tempList = new ArrayList<String>(); // Used to feed arrays into masterQueue
      ArrayList<String> deepCopyList = new ArrayList<String>();
     
     // The initial states: tempList is going to include the start word. 
     // Then, it will be added to the masterQueue. 
      tempList.add(startWord);
      masterQueue.add(tempList);
      
      // Ensures we don't loop back to the startWord again. 
      myWordList.setValue(endWord, myWordList.UNUSED); 
      myWordList.setValue(startWord, myWordList.USED);
            
      while (masterQueue.size() > 0){ // while the word is not found.   
         tempList = masterQueue.get(0);
         
         // This manually savaes the current array to be searched, tempList, into the deepCopy.
         // Somehow, deepCopyList = tempList did not work. 
         deepCopyList = new ArrayList<String>();
         for (String element: tempList) 
         {
            deepCopyList.add(element);
         }
         
         // get last word of the first list in queue. 
         String currentWord = tempList.get(tempList.size()-1); 
         masterQueue.remove(0); // Pop the first index. 
         neighbors = myWordList.getNeighbors(currentWord);
         
         for (int i = 0; i < neighbors.size(); i++)
         {
            if (neighbors.get(i).equals(endWord))
            {
               tempList.add(neighbors.get(i));
               return tempList;
            } 
            else if (myWordList.isUsed(neighbors.get(i)) == myWordList.UNUSED)
            {
               wordsVisited++;
               tempList.add(neighbors.get(i));
               myWordList.setValue(neighbors.get(i), myWordList.USED);
               masterQueue.add(tempList);
               
               // Again, manually retrieving values from deepCopyList. 
               tempList = new ArrayList<String>();
               for (String element: deepCopyList) 
               {
                  tempList.add(element);
               }
            }           
         }        
      }
      
      // Returns an empty array if there is no solution. 
      return new ArrayList<String>();
   }
   

   /**
   runs the program
   */
   public void run()
   {
      Scanner reader = new Scanner(System.in);
      System.out.print("Enter starting word: ");
      String startWord = reader.nextLine();
      System.out.print("Enter ending word: ");
      String endWord = reader.nextLine();
      
      ArrayList<String> ladder = this.getLadder(startWord, endWord);
      
      if(ladder.size() == 0)
      {
         System.out.println("No solution found!");
      }
      else
      {
         System.out.println("Solution = " + ladder);
      }
      
      System.out.println("Nodes Visited: " + wordsVisited);
      
      // Resetting the dictionary, and rerunning the search. 
      System.out.println("Run again? (y/n): ");
      String restartInput = reader.nextLine();
      System.out.println("");
      if (restartInput.equals("y")) 
      {
         myWordList = new Dictionary();
         run();
      } 
      else 
      {
         System.out.println("Thanks for playing");
      }
   }
   
   
   public static void main(String[] args)
   {
      BFSLadder test = new BFSLadder();
      test.run();
   }
}