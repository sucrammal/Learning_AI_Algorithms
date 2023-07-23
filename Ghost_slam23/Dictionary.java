import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

/**
list of words for ghost game
@author Roger Wistar
@version 2023/02/03
*/
public class Dictionary
{
   /** word has been used */
   public static final int USED = 1;
   
   /** useful if someone tries to find an invalid word */
   public static final int ERROR = 0;
   
   /** word has not been used */
   public static final int UNUSED = -1;

   /** list of all the words */
   protected HashMap<String, Integer> wordList;
   
   /** keys in the word list */
   protected ArrayList<String> keys;
   
   /**
   default constructor
   */
   public Dictionary()
   {
      wordList = new HashMap<String, Integer>();
      
      try
      {
         Scanner reader = new Scanner(new File("corncob.txt"));
         
         while(reader.hasNextLine())
         {
            String word = reader.nextLine();
            wordList.put(word.toLowerCase(), UNUSED);   
         }
         
         System.out.println("# words read = " + wordList.size());
         
         keys = new ArrayList<String>(wordList.keySet());
      }
      catch(IOException ioe)
      {
         System.out.println(ioe);
         System.exit(1);
      }
   }
   
   /**
   checks if word parameter has been used
   @param word the word to check
   @return USED or UNUSED if word exists, 
   else ERROR if not found 
   */
   public int isUsed(String word)
   {
      if(wordList.containsKey(word) == false)
      {
         return ERROR;
      }
      else
      {
         return wordList.get(word);
      }
   }
   
   /**
   changes value of given word
   @param word the word to change
   @param value the updated value
   @return new value, else ERROR if not found
   */
   public int setValue(String word, int value)
   {
      if(wordList.containsKey(word) == false)
      {
         return ERROR;
      }
      else
      {
         wordList.put(word, value);
         return wordList.get(word);
      }
   }
   
   /**
   prints out all key-value pairs
   */
   public void printDictionary()
   {
      for(String word: wordList.keySet())
      {
         int value = wordList.get(word);
         System.out.println(word + " = " + value);
      }
   }
   
   /**
   gets all the letters that can make valid successor words
   @param word the prefix to search from
   @return ArrayList containing all valid letters to add to this prefix
   */
   public ArrayList<String> getNeighbors(String word)
   {
      ArrayList<String> neighbors = new ArrayList<String>();
      
      char nextLetter = 'a';
      
      for(int letIdx = 1; letIdx <= 26; letIdx++)
      {
         String newWord = word + nextLetter;
         
         boolean found = false;
         int idx = 0;
         while(!found && idx < keys.size())
         {
            String searchWord = keys.get(idx);
            if(searchWord.length() >= 4 && searchWord.startsWith(newWord))
            {
               neighbors.add("" + nextLetter);
               found = true;
            }
            else
            {
               idx++;
            }
         }
         
         nextLetter++;
         
         // System.out.println(keys);
      }
   
      // System.out.println(neighbors);
      return neighbors;
   }            
   
   public static void main(String[] args)
   {
      Dictionary test = new Dictionary();
      // test.printDictionary();
      
      ArrayList<String> nbrs = test.getNeighbors("ed");
      System.out.println(nbrs);
   }
   
}