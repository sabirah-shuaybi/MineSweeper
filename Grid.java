import java.awt.*;
import objectdraw.*;
/**
 * Grid class constructs a grid of GridCells. Grid class is in charge of placing random mines across
 * the grid as well as setting all the non-mine cells with a count of neighboring mines. This class
 * contains knowledge of the state of the grid such as how many mines are present, how many flags,
 * how many mines have been flagged, whether the game is still in session or whether player has
 * won or lost. The Grid constructor takes a int level (level of difficulty that player chooses)
 * and distributes this value throughout its methods to construct a larger, more challenging grid
 * with more randomly placed mines (if player chooses harder levels).
 *
 * @author Sabirah Shuaybi
 * @version 11/29/16
 */

public class Grid
{
    //Start location (both x and y coord) of the grid construct
    private static final int START_LOC = 20;

    //Size of an individual grid cell
    private static final int CELL_SIZE_MULTIPLIER = 30;

    private DrawingCanvas canvas;
    private FilledRect mainRect;
    private GridCell [][] cellArray;
    private int level;

    //To keep track of whether game is in progress or game is over
    private boolean gameLost = false;
    private boolean gameWon = false;

    /* Grid constructor also takes level as parameter to decided how to construct the grid (size)
    as well as the number of mines to randomly place on the grid */
    public Grid(DrawingCanvas canvas, int level) {

        //Save reference to canvas to use throughout class
        this.canvas = canvas;
        this.level = level;

        createGridCells(level);
        randomizeMines(level);

        setAllNeighborMineCounts();
    }

    /* Creates a dynamic grid whose size will vary depending on level of difficulty */
    private void createGridCells(int n) {

        //Create a main underlying grid box of color gray
        mainRect = new FilledRect
        (START_LOC, START_LOC, (n * CELL_SIZE_MULTIPLIER), (n * CELL_SIZE_MULTIPLIER), canvas);
        mainRect.setColor(Color.GRAY);
        mainRect.sendToBack();

        //Create and initialize 2D array of cells
        cellArray = new GridCell[n][n];

        //Initial coordinates for a GridCell
        int left = START_LOC;
        int top = START_LOC;

        int counter = 0;

        //Loop through array and initialize every element with a GridCell
        for(int i=0; i<cellArray.length; i++) {
            for(int j=0; j<cellArray[i].length; j++) {
                cellArray[i][j] = new GridCell(left, top, canvas);

                //Increment counter after creation of evey new GridCell
                counter++;

                //Shift x coord of cell to the left of previous cell
                left += GridCell.CELL_SIZE;

                //If n # of cells have been constructed, move construction to the next row
                    //Note: value of n could be 10, 15, or 20 depending on level
                if(counter%n == 0) {
                    //Reset x coord at the start of each new row
                    left = START_LOC;
                    //Move y coord of cell down from previous row of cells
                    top += GridCell.CELL_SIZE;
                }
            }
        }

    }

    /* Searches through the array of GridCells and returns the one that was clicked on */
    public GridCell getClickedCellAt(Location point) {

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray[i].length; j++) {

                if(cellArray[i][j].contains(point)) {
                    //Exit method as soon as the clicked cell is found
                    return cellArray[i][j];
                }
            }
        }
        //If click wasn't contained within any cell, return null
        return null;
    }

    /* Randomly places a certain number of mines on the grid
    (The higher the level of difficulty, the more mines will be present)*/
    private void randomizeMines(int n) {

        //Construct a RandomIntGenerator ranging from 0-9 (index values of cell array)
        RandomIntGenerator randomInt = new RandomIntGenerator(0, cellArray.length-1);

        int counter = 0;

        //Ensure that no more or no less than 10 mines are being assigned
        while (counter <= n-1) {

            int indexRow = randomInt.nextValue();
            int indexCol = randomInt.nextValue();

            //First check to see if the randomly generated location is unique
                //from all previous random locations in loop
                //Significance: to prevent more than one mine being assigned to a cell
            if (!(cellArray[indexRow][indexCol].containsMine())) {

                //Construct the mine at this random index
                cellArray[indexRow][indexCol].makeMine();

                //Increment counter after creation of each UNIQUELY placed mine
                counter++;

            }

        }

    }

    /* Checks if neighboring grid cells contain mines and if so, totals neighboring mines
    Takes a 2D index value [i][j] as parameters so it can assess the
    surrounding neighbors of the index passed in */
    private int getNeighborMineCount(int i, int j) {

        int neighborCount = 0;

        //The following if conditions ensure that the neighbor counts for
            //border grid cells won't cross the bounds of the grid

        if(i-1 >= 0 && j-1 >= 0) {
            if(cellArray[i-1][j-1].containsMine())
                neighborCount++;
        }

        if(i-1 >= 0) {
            if(cellArray[i-1][j].containsMine())
                neighborCount++;
        }

        if(i-1 >= 0 && j+1 <= cellArray.length-1) {
            if(cellArray[i-1][j+1].containsMine())
                neighborCount++;
        }

        if(j-1 >= 0) {
            if(cellArray[i][j-1].containsMine())
                neighborCount++;
        }

        if(j+1 <= cellArray.length-1) {
            if(cellArray[i][j+1].containsMine())
                neighborCount++;
        }

        if(i+1 <= cellArray.length-1 && j-1 >= 0) {
            if(cellArray[i+1][j-1].containsMine())
                neighborCount++;
        }

        if(i+1 <= cellArray.length-1) {
            if(cellArray[i+1][j].containsMine())
                neighborCount++;
        }

        if(i+1 <= cellArray.length-1 && j+1 <= cellArray.length-1) {
            if(cellArray[i+1][j+1].containsMine())
                neighborCount++;
        }

        //Return the total number of neighbors with mines
        return neighborCount;
    }

    /* Evaluates neighbor count and sets all counts for all cells
    Sets the counts but actual number is hidden (only displayed on a left-click */
    private void setAllNeighborMineCounts() {

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray[i].length; j++) {

                if(!(cellArray[i][j].containsMine())) {

                    int neighborCount = getNeighborMineCount(i,j);

                    cellArray[i][j].setNeighborMineCount(neighborCount);
                }
            }
        }

    }

    /* Searches through array of GridCells and returns the total number of flags present */
    public int countFlags() {

        int flagCount = 0;

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray[i].length; j++) {

                //Increment flagCount if cell at given index contains a flag
                if(cellArray[i][j].containsFlag()) {

                    flagCount++;
                }
            }
        }
        return flagCount;
    }

    /* Determines if all present mines have been located or flagged by player */
    private boolean allMinesLocated() {

        int n = 0;

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray[i].length; j++) {

                if(cellArray[i][j].flaggedMine()) {
                    n++;
                }
            }
        }
        //Number of mines to be flagged depends on level
        return (n == level);
    }

    /* Determines if all non-mine cells have been uncovered by player */
    private boolean allCellsUncovered() {

        int n = 0;

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray.length; j++) {

                if(cellArray[i][j].countRevealed()) {
                    n++;
                }
            }
        }

        //Number of non-mines depends on the
            //total number of grid cells (level*level) minus the number of mines(aka level)
        return (n == ((level*level)-level));

    }

    /* Keeps track of and updates the status of the game/grid with each LEFT click */
    public void handleLeftClick(GridCell cell) {

        //Do not process/ignore left clicks if game status = over
            //(aka if user either won or lost)
        if (gameWon || gameLost) return;

        //Ask cell that was left-clicked on to handle click
            //Further delegation of left click
        cell.handleLeftClick();

        //If player had exploded a mine, set gameLost status to true
        if (cell.isExploded()) {
            gameLost = true;
            //Reveal all the hidden mines
            displayAllMines();
        }
    }
    /* Keeps track of and updates the status of the game/grid with each RIGHT click */
    public void handleRightClick(GridCell cell) {

        //Do not process/ignore right clicks if game status = over
            //(aka if user either won or lost)
        if (gameWon || gameLost) return;

        //Ask cell that was right-clicked on to handle click
        cell.handleRightClick();

        //Ensure that the only way player wins is if all mines are found/flagged
            //AND if all non-mine cells have been uncovered
        if (allMinesLocated() && allCellsUncovered()) {
            gameWon = true;
            displayAllMines();
        }

    }

    /* Determines if the game is over */
    public boolean isGameOver() {

        //There are two ways game = over --> player wins OR player loses
        return gameWon || gameLost;
    }

    /* Uncovers all hidden mines */
    public void displayAllMines() {

        for(int i=0; i<cellArray.length; i++) {

            for(int j=0; j<cellArray[i].length; j++) {

                if(cellArray[i][j].containsMine()) {

                    cellArray[i][j].showMine();
                }
            }
        }
    }

    /* Displays a message to player regarding either a win or a loss */
    public String getStatusMessage() {

        if (gameLost)
            return "Mine exploded! You lost!";

        if (gameWon) {
            return "Great job! You won!";
        }

        //Don't display any message if game is still in progress
        return null;

    }

}
