
/**
 * MineSweeper Class
 * 
 * @author James Sonntag 
 * @version 1/30/2011
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class minesweeper extends JFrame
{
    // this final variable defines the # of elements in the grid in each direction
    private int my_x;
    private int my_y;
    private boolean hasmine;
    private boolean hastried;
    private static final int boardsize = 10;

    // this final variable defines the number of grid positions on the board that have a mine on them
    private static final int nummines = 10;

    public static final int Empty = 0;
    public static final int Mine = -1;
    public static final int Flag = -2;
    public static final int FlaggedMine = -3;
    public static final int UncoveredMine = -4;
    public static final int UncoveredEmpty = -5;

    private int mines = nummines;

    // this final variable defines the size of the buttons
    private final int gridsize = 45;

    // this is the 2 dimensional array of the buttons for the board
    private JButton[][] buttons;

    private int[][] mineBoard;

    // this is a label that tells us how many points the user has scored
    private JLabel scorelabel = new JLabel("0 points");

    // this is a label that tells us if the game is going or not
    private JLabel status = new JLabel("the game is afoot");

    // this is the 2-dimensional array of gridposition elements to track information about the grid
    //private gridposition[][] thegrid;

    // this is the instance variable to track the current score (start off at 0)
    private int score = 0;

    // this is the instance variable that tells us if the game is going or not
    private boolean gamegoing = true;

    public minesweeper() {
        super("Mine Sweeper");
        // this is so Java will close our program if we close the window (should be default, but isn't)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // this creates the 2-d arrays as having boardsize*boardsize elements
        buttons = new JButton[boardsize][boardsize];

        mineBoard = new int[10][10];
        // this sets the size of the window in x, y terms
        setSize(boardsize * gridsize + 60, boardsize * gridsize + 180);
        // this lets us place items on the window as we want to
        getContentPane().setLayout(null);

        // this puts our scorelabel on the window
        getContentPane().add(scorelabel);
        // this has us place it where we want it
        scorelabel.setBounds(30,30+gridsize*boardsize,130,30);

        // this puts our status lable on the window
        getContentPane().add(status);
        // this has us place it where we want it
        status.setBounds(160,30+gridsize*boardsize,130,30);

        // this nested for loop creates the neccesary button objects, the neccesary gridposition
        // objects, set the needed values for the gridposition objects (telling them what their x and
        // y values are, etc) and assigns an actionListener for the buttons passing in the x and
        // y values for the gridclick objects so they know what x and y they belong to
        for (int a = 0; a < boardsize; a++) 
            for (int b = 0; b < boardsize; b++) {
                buttons[a][b] = new JButton("");
                getContentPane().add(buttons[a][b]);
                buttons[a][b].setBounds(30+gridsize*a,30+gridsize*b,gridsize,gridsize);
                buttons[a][b].addMouseListener(new MyMouseListener(a,b));
                //buttons[a][b].addActionListener(new gridclick(a,b));
                //thegrid[a][b] = new gridposition();
                setx(a);
                sety(b);
                settried(false);
                setmine(false);
        }
        // this calls the method secretly places mines on the grid
        assignmines();
        // this makes it so we can see the window.
        setVisible(true);
    }

    public static void main(String[] args) {         
        new minesweeper();
    }

    // the following method will place nummines (a final variable 
    // defined above) mines on the board in random locations.
    public void assignmines() {

        for(int row = 0; row < mineBoard.length; row++) {
            for(int col = 0; col < mineBoard[0].length; col++) {
                mineBoard[row][col] = 0;
            }
        }

        int minesPlaced = 0;
        Random t = new Random();
        while(minesPlaced < nummines) {
            int row = t.nextInt(10);
            int col = t.nextInt(10);
            if(mineBoard[row][col] == Empty) {
                setmine(true);
                mineBoard[row][col] = Mine;
                minesPlaced++;
            }
        }
    }
    //This method tells the system what Characters the Values have.
    static char getUserChar(int cellValue) {
        if(cellValue == Mine) {
            return 'X';
        } else if( cellValue == Empty) {
            return '+';
        } else if (cellValue == Flag || cellValue == FlaggedMine) {
            return 'F';
        } else if (cellValue == UncoveredMine) {
            return 'X';
        } else { String adjMines = Integer.toString(cellValue);
            return adjMines.charAt(0);
        }
    }
    //This method shows how many mines are around the spot that was clicked
    static int numAdjMines(int[][] mineBoard, int row, int col) {
        int numMines = 0;

        for(int dr = -1; dr <= 1; dr ++) {
            for(int dc = -1; dc <= 1; dc++) {
                if(row + dr >= 0 && row + dr < mineBoard.length &&
                col+ dc >= 0 && col + dc < mineBoard[0].length) {
                    if(mineBoard[row+dr][col+dc] == Mine ||
                    mineBoard[row+dr][col+dc] == FlaggedMine) {
                        numMines++;
                    }
                }
            }
        }
        return numMines;
    }

    // This method takes in an x and y value and defines what should happen when the user clicks there. 
    public void click(int row, int col) {
        if(mineBoard[row][col] == Mine) {
            buttons[row][col].setText(Character.toString(getUserChar(mineBoard[row][col])));
            lose();
        } else {
            score += 1;
            updatescore();
            buttons[row][col].setText("" + numAdjMines(mineBoard, row, col));
            mineBoard[row][col] = UncoveredEmpty;
            //buttons[row][col].setText(Character.toString(getUserChar(mineBoard[row][col])));
            if(numAdjMines(mineBoard, row, col) == Empty) {
                for(int dr = -1; dr <= 1; dr ++) {
                    for(int dc = -1; dc <= 1; dc++) {
                        if(row+dr >= 1 && row+dr < 10 &&
                        col+dc >= 1 && col+dc < 10) {
                            if(mineBoard[row+dr][col+dc] == Empty) {
                                click(row+dr,col+dc);
                            }
                        }
                    }
                }
            }
        }
    }

    // a method to update the score, when the score gets to 80 points, you win!
    public void updatescore() {
        scorelabel.setText("" + score + " points");
        if (100 - score <= 10) win();
    }
    // this method updates the status text to explain that you lost, and then change the instance variable
    // that states that the game is going, and make it false
    public void lose() {
        status.setText("You're finished");
        gamegoing = false;
    }

    public void win() {
        status.setText("You won!");
        gamegoing = false;
    }

    public void setx(int paramInt) {
        my_x = paramInt;
    }

    public void sety(int paramInt) {
        my_y = paramInt;
    }

    public int getx() {
        return my_x;
    }

    public int gety() {
        return my_y;
    }

    public void settried(boolean paramBoolean) {
        hastried = paramBoolean;
    }

    public boolean gettried() {
        return hastried;
    }

    public void setmine(boolean paramBoolean) {
        hasmine = paramBoolean;
    }

    public boolean getmine() {
        return hasmine;
    }
    //This class defines what happens when the user clicks on a button.
    private class MyMouseListener extends MouseAdapter {
        private int x = 0;
        private int y = 0;

        public MyMouseListener(int row, int col) {
            this.x = row;
            int i = 0;
            this.y = col;
        }

        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                if((mineBoard[x][y] == Empty) && (minesweeper.this.gamegoing == true)) {
                    minesweeper.this.click(x, y);
                } else if(mineBoard[x][y] == Mine) {
                    buttons[x][y].setText(Character.toString(getUserChar(mineBoard[x][y])));
                    minesweeper.this.lose();

                }} else if(e.getButton() == MouseEvent.BUTTON3) {
                minesweeper.this.buttons[x][y].setText("F");
            }
        }
    }

}
