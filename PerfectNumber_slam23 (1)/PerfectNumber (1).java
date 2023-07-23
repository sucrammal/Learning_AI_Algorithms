import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

 
/** 
Searches for a perfect number given a specific limit to how big the numbers can get 
@author Marcus Lam
@version 2023/02/21
*/
public class PerfectNumber 
{ 
   
   /**
   checks if a given number is perfect
   @num the number to check
   @return true if perfect, else false
   */
   public static boolean isPerfect(int num) {
      int sum = 1; // 1 is always going to be a factor. Include this in the initial sum. 
      
      for (int i = 2; i < num; i++) 
      {
         if (num % i == 0) 
         {
            sum = sum + i;
         }
      }
      
      if (sum == num) 
      {
         return true; 
      } 
      
      else 
      {
         return false; 
      }
   
   }
   
   /**
   tries to find a perfect number within the given range
   @param limit the upper limit to check (10^limit)
   @return the first perfect number found, or -1 if none found
   */
   public static int findPerfect(int limit) 
   {
      ArrayList<Integer> masterStack = new ArrayList<Integer>();
      int currentNumber = 0;
      int maxLimit = (int)Math.pow(10, limit); 

      masterStack.add(currentNumber); 
      
      while (masterStack.size() != 0) 
      {
         currentNumber = masterStack.remove(masterStack.size()-1);
         if (isPerfect(currentNumber)) 
         {
            return currentNumber;
         } 
         else if (currentNumber < maxLimit)
         {
            for (int i = 0; i <= 9; i++) 
            {
               masterStack.add((currentNumber * 10) + i);
            }
         }
        
      }
      
      return -1;
   }
   
   
   
   public static void main(String[] args) 
   {
      Scanner reader = new Scanner(System.in);
      PerfectNumber test = new PerfectNumber();
      System.out.println("Select your upper limit: 10^___");
      String userInput = reader.nextLine();
      System.out.println(test.findPerfect(Integer.parseInt(userInput)));
   }

}

   
