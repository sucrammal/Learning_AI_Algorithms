import java.util.Scanner;

/**
simple tic-tac-toe program
@author Roger Wistar
@version 2021/12/22
*/
public class TicTacToe
{
   public static void main(String[] args)
   {
      Scanner reader = new Scanner(System.in);
   
      final int NO_MOVE = 0;
      final int PLAYER_X = 1;
      final int PLAYER_O = -1;
      final int TIE_GAME = 2;
      
      final char[] BOARD_MARKS = { 'O', '-', 'X' };
   
      int whoseTurn = PLAYER_X;
      
      int[][] board = new int[3][3];
      
      int numMoves = 0;
      boolean isPlaying = true;
      while(isPlaying)
      {
         for(int row = 0; row < 3; row++)
         {
            for(int col = 0; col < 3; col++)
            {
               if(board[row][col] == NO_MOVE)
               {
                  System.out.print("" + (row * 3 + col) + " ");
               }
               else
               {
                  System.out.print(BOARD_MARKS[board[row][col] + 1] + " ");
               }
            }
            System.out.println();
         }
         
         int gameState = NO_MOVE;
         if(board[0][0] == board[0][1] && board[0][1] == board[0][2]
            && board[0][0] != NO_MOVE)
         {
            gameState = board[0][0];
         }
         if(board[1][0] == board[1][1] && board[1][1] == board[1][2]
            && board[1][0] != NO_MOVE)
         {
            gameState = board[1][0];
         }
         if(board[2][0] == board[2][1] && board[2][1] == board[2][2]
            && board[2][0] != NO_MOVE)
         {
            gameState = board[2][0];
         }
         if(board[0][0] == board[1][0] && board[1][0] == board[2][0]
            && board[0][0] != NO_MOVE)
         {
            gameState = board[0][0];
         }
         if(board[0][1] == board[1][1] && board[1][1] == board[2][1]
            && board[0][1] != NO_MOVE)
         {
            gameState = board[0][1];
         }
         if(board[0][2] == board[1][2] && board[1][2] == board[2][2]
            && board[0][2] != NO_MOVE)
         {
            gameState = board[0][2];
         }
         if(board[0][0] == board[1][1] && board[1][1] == board[2][2]
            && board[0][0] != NO_MOVE)
         {
            gameState = board[0][0];
         }            
         if(board[0][2] == board[1][1] && board[1][1] == board[2][0]
            && board[0][2] != NO_MOVE)
         {
            gameState = board[0][2];
         }
         if(numMoves == 9)
         {
            gameState = TIE_GAME;
         }
         
         if(gameState != NO_MOVE)
         {
            if(gameState == TIE_GAME)
            {
               System.out.println("It's a tie!");
            }
            else
            {
               System.out.println("Player " + BOARD_MARKS[gameState + 1]
                  + " has won!");
            }
            System.out.println();
            
            System.out.print("Play again? (Y/N) ");
            String input = reader.nextLine();
            
            if(input.equals("N"))
            {
               System.exit(0);
            }
            else
            {
               for(int row = 0; row < 3; row++)
               {
                  for(int col = 0; col < 3; col++)
                  {
                     board[row][col] = NO_MOVE;
                  }
               }
            
               numMoves = 0;
               whoseTurn = PLAYER_X;
            } 
         }
         else
         {
         
         
            System.out.println();
         
            System.out.print("Player " + BOARD_MARKS[whoseTurn + 1] +
               ", enter square to move: ");
            String input = reader.nextLine();
            int squareNum = Integer.parseInt(input);
            int row = squareNum / 3;
            int col = squareNum % 3;
         
            board[row][col] = whoseTurn;
         
            if(whoseTurn == PLAYER_X)
            {
               whoseTurn = PLAYER_O;
            }
            else
            {
               whoseTurn = PLAYER_X;
            }
         
            numMoves++;
         }
      }
   
   
   
   
   
   
   }







}