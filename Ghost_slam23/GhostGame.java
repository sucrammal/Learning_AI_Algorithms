import java.util.ArrayList;
import java.util.Scanner;

/**
program to play "ghost" word game using minimax
@author Marcus Lam
@version Feb 21
*/
public class GhostGame
{
   /** it's the human's turn */
   public static final int HUMAN = 1;
   
   /** it's the computer's turn */
   public static final int COMP = -1;
   
   /** max depth to let the algorithm search */
   public static final int START_DEPTH = 2;
   
   /** code for the MAX player (same as HUMAN) */
   public static final int MAX = 1;
   
   /** code for the MIN player (same as COMP) */
   public static final int MIN = -1;
   
   /** list of words to consider */
   private Dictionary myWordList;
   
   /** Stores the number of neighbors visisted after each minimax call */
   public int nodesVisited = 0; 
   
   /**
   default constructor
   */
   public GhostGame()
   {
      myWordList = new Dictionary();
   
   }
   
   /**
   makes the best choice using minimax algorithm
   @param word the stem word
   @param depth the remaining depth to search
   @param whoseTurn MIN or MAX
   @param lastMove the last move made
   @return the best letter to add right now
   */
   public Move minimax(String word, int depth, int whoseTurn, String lastMove) {
   
      double bestScore = 0;
      String bestNextMove = "";  
      
      if ((myWordList.isUsed(word) != Dictionary.ERROR)&&(word.length() >= 4)) // Win/loss terminal state
      {
         System.out.println("minimax(" + word + ", " + depth + ", " + whoseTurn + ", " + lastMove + ")" + " returned: " + "[" + lastMove + "," + (whoseTurn * 100) + "]");
         return new Move(lastMove, whoseTurn * 100);  
      } 
      
      else if (depth == 0) // cutoff depth reached. 
      {
         System.out.println("minimax(" + word + ", " + depth + ", " + whoseTurn + ", " + lastMove + ")" + " returned: " + "[" + lastMove + "," + (myWordList.getNeighbors(word).size()) + "]");
         return new Move(lastMove, whoseTurn * myWordList.getNeighbors(word).size()); // an estimate of winning score. 
      } 
      
      else 
      {
         bestScore = whoseTurn * -1 * Integer.MAX_VALUE; // This sets the best score initially to the "minimum" relative to Min/Max. E.g., the most negative value for MAX layer. 
         bestNextMove = "";
         String newWord = word; // make a copy of the current word. 
         
         for (String neighbor: myWordList.getNeighbors(word)) // get all child nodes of the current move. 
         {
            nodesVisited++; 
            newWord = newWord + neighbor;
            System.out.println("minimax(" + newWord + ", " + (depth-1) + ", " + (whoseTurn * -1) + ", " + neighbor + ")");
            Move bestNeighbor = minimax(newWord, depth - 1, whoseTurn * -1, neighbor); // as we move down the tree, depth will decrease as we go deeper down the tree and we will change the turns every layer we deepen. 
            newWord = word; 
            
            // Now, we need to check for our best move / child node to select, depending on MIN or MAX 
            if (((bestNeighbor.score > bestScore)&&(whoseTurn == MAX))||((bestNeighbor.score < bestScore)&&(whoseTurn == MIN)))
            {
               bestScore = bestNeighbor.score; 
               bestNextMove = neighbor; // we want this to be the best letter we possibly chose. Instead of getting the best move of the current neighbor, not 5 neighbors ahead in a terminal state. 
            }

         }
         
         System.out.println(" returned: " + "[" + bestNextMove + "," + bestScore + "]");
         return new Move(bestNextMove, bestScore);
                  
      }
   
   }


   
   /**
   runs the program
   */
   public void run()
   {
      Scanner reader = new Scanner(System.in);
      
      String word = "";
      
      int whoseTurn = 1;
      boolean gameOver = false;
      
      while(!gameOver)
      {
         String letter;
      
         if(whoseTurn == HUMAN)
         {
            System.out.print("Human player, enter a letter: ");
            letter = reader.nextLine();
            word += letter;
            System.out.println("Player made " + word);
         }
         else
         {
            // run miniMax
            Move compMove = minimax(word, START_DEPTH, MIN, "");
            word += compMove.letter;
            
            /** remove this line when you are ready to test */
           //  word += "e";
            System.out.println("Nodes visited: " + nodesVisited);
            nodesVisited = 0;
            System.out.println("Computer made " + word);
         }
         
         if(word.length() >= 4 && myWordList.isUsed(word) == Dictionary.UNUSED)
         {
            gameOver = true;
         }
         else
         {
            whoseTurn = (whoseTurn * -1);
         }
      }
      
      System.out.println("Final word = " + word);
      
      if(whoseTurn == HUMAN)
      {
         System.out.println("Sorry, you lose!"); 
      }
      else
      {
         System.out.println("Congrats, you win!");
      }
   }
   
   
   public static void main(String[] args)
   {
      GhostGame test = new GhostGame();
      test.run();
   }
   
   /**
   helper class for minimax
   */
   private class Move
   {
      /** the letter being considered */
      public String letter;
      
      /** the score resulting from this move */
      public double score;
      
      /**
      constructor function
      @param let the letter
      @param scr the score 
      */
      public Move(String let, double scr)
      {
         letter = let;
         score = scr;
      }
      
      /**
      represents the move as a string
      @return class variables in a string
      */
      public String toString()
      {
         return "[" + letter + "," + score + "]";
      }
   }
}