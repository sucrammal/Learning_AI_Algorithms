import java.util.Scanner;

public class tictactoe
{
    // These are the integer states that will be in the actual board array. 
    // Copied from the solution code to save time. 
    public static final int NO_MOVE = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = -1;
    public static final int TIE_GAME = 2;

    public static final char[] BOARD_MARKS = { 'O', '-', 'X' }; // This will be useful when printing the board. 

    private int[] board; 
    private int whoseTurn;
    private boolean isPlaying;

    public tictactoe() {
        resetGame();
    }

    private void resetGame(){
        isPlaying = true;
        whoseTurn = PLAYER_X;
        board = new int[9];
        printBoard(board);
    }

    private void printBoard(int[] board) {
        for (int row = 0; row < 3; row++){
            for (int term = 0; term < 3; term++) {
                if (board[term] == NO_MOVE) {
                    System.out.print(" " + (term +row * 3) + " ");
                } else if (board[term] == PLAYER_X) {
                    System.out.print(" " + BOARD_MARKS[2] + " ");
                } else { // if PLAYER_O
                    System.out.print(" " + BOARD_MARKS[0] + " ");
                }
        }
        System.out.println();
    }

}

    public static void main(String[] args) {
       tictactoe newGame = new tictactoe();
    }


}

