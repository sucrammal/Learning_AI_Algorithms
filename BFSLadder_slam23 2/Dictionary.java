import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

/**
solves word ladders using breadth-first search; 
@author Marcus Lam
@version 2022/01/26
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
   
   /** default constructor */
   public Dictionary()
   {
      wordList = new HashMap<String, Integer>();
      
      try
      {
         Scanner reader = new Scanner(new File("fivewords.txt"));
         
         while(reader.hasNextLine())
         {
            String word = reader.nextLine();
            wordList.put(word.toLowerCase(), UNUSED);   
         }
         
         System.out.println("# words read = " + wordList.size());
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
   returns all valid words that are one letter away from param
   @param word the base word to search from
   @return ArrayList<String> containing all unused legal successors
   */
   public ArrayList<String> getNeighbors(String word)
   {
      ArrayList<String> neighbors = new ArrayList<String>();
   
      char[] letters = word.toCharArray();
      
      for(int idx = 0; idx < letters.length; idx++)
      {
         char originalLetter = letters[idx];
         
         letters[idx] = 'a';
         
         for(int letIdx = 1; letIdx <= 26; letIdx++)
         {
            String newWord = new String(letters);
            boolean same = word.equals(newWord);
            
            if(word.equals(newWord) == false
               && wordList.containsKey(newWord) == true)
            {
               //System.out.println(newWord);
               neighbors.add(newWord);
            
            }
            
            letters[idx]++;
         }
         
         letters[idx] = originalLetter;
      }
   
      return neighbors;
   }            
   
   public static void main(String[] args)
   {
      Dictionary test = new Dictionary();
      test.printDictionary();
   }
   
}