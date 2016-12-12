import java.awt.*;
import objectdraw.*;
/**
 * The GridCell class stores and remembers information about an individual cell. This information
 * includes aspects such as whether the cell contains a mine, the number of neighbors that do contain
 * mines in a non-mine cell, whether the cell has been flagged, whether the cell has been revealed, or
 * exploded etc. The class also takes care of all the formatting (centering, positioning) required
 * with each possible component in a cell (mine, flag, neighbor count, white backdrop for neighbor count
 * and exploded backdrop for exploded cell).
 *
 * @author Sabirah Shuaybi
 * @version 11/29/16
 */
public class GridCell
{
    //Potential components of a GridCell object
    private FramedRect cell;
    private FilledOval mine;
    private Text numText;
    private FilledRect flagRect;

    //Center of the cell
    private double cellCenterX;
    private double cellCenterY;

    public static final int CELL_SIZE = 30;
    private static final int MINE_SIZE = 15;

    //To ensure that objects filling a grid cell will not cover up grid lines
    public static final double CELL_BUFFER = 1.48;
    public static final double CELL_SIZE_ADJUSTED = CELL_SIZE - 1.3;

    //Keep track of state of cell
    private boolean exploded = false;
    private boolean minePresent = false;
    private boolean flagPresent = false;
    private boolean countRevealed = false;

    private int neighborMineCount;
    private DrawingCanvas canvas;
    private int x;
    private int y;

    public GridCell(int x, int y, DrawingCanvas canvas) {

        //Save references into instance variables
        this.x = x;
        this.y = y;
        this.canvas = canvas;

        cell = new FramedRect(x, y, CELL_SIZE, CELL_SIZE, canvas);

        //Compute coordinates for center of grid cell
        cellCenterX = cell.getX() + (cell.getWidth()/2);
        cellCenterY = cell.getY() + (cell.getHeight()/2);
    }

    /* Determines if user clicked on gridcell */
    public boolean contains(Location point) {

        return cell.contains(point);
    }

    /* Determines if cell contains a mine */
    public boolean containsMine() {

        return minePresent;
    }

    /* Determines if a flag has been placed on a grid cell */
    public boolean containsFlag() {

        return flagPresent;
    }

    /* Determines if the contents of a non-mine cell (aka neighbor count)
    have been revealed to the player */
    public boolean countRevealed() {

        return countRevealed;
    }

    /* Constructs a mine at center of cell and informs state of cell that mine is now present */
    public void makeMine() {

        mine =  new FilledOval(x, y, MINE_SIZE, MINE_SIZE, canvas);

        //Compute coordinates for center of mine relative to center of grid cell
        double mineCenterX = cellCenterX - (mine.getWidth()/2);
        double mineCenterY = cellCenterY - (mine.getHeight()/2);

        mine.moveTo(mineCenterX, mineCenterY);
        mine.hide();

        //Set the state to one that contains a mine
        minePresent = true;

    }

    /* Creates and centers the number of mine neighbors within cell */
    public void setNeighborMineCount(int count) {

        String countString;

        //If there are no neighboring mines, display an empty text object rather than a 0
        if (count == 0)
            countString = "";
        //Else, display an actual number of neighboring mines
        else
            countString = count +"";

        numText = new Text(countString, x, y, canvas);

        //Increase font size (original text size = 13)
        numText.setFontSize(25);

        //Make numText bold
        numText.setBold(true);

        //Compute center of text based on cell center
        double numTextCenterX = cellCenterX - (numText.getWidth()/2);
        double numTextCenterY = cellCenterY - (numText.getHeight()/2);

        //Move numText to this calculated center
        numText.moveTo(numTextCenterX, numTextCenterY);

        //Hide number since we only want it revealed on a left-click
        numText.hide();

    }

    /* Creates a green rectangular flag on top of grid cell */
    private void createFlagRect() {

        flagRect = new FilledRect
        (x+CELL_BUFFER, y+CELL_BUFFER, CELL_SIZE_ADJUSTED, CELL_SIZE_ADJUSTED, canvas);

        flagRect.setColor(Color.GREEN);
    }

    /* Creates a white background for a non-mine grid cell */
    private void createWhiteBackDrop() {

        FilledRect backdrop = new FilledRect
            (x+CELL_BUFFER, y+CELL_BUFFER, CELL_SIZE_ADJUSTED, CELL_SIZE_ADJUSTED, canvas);

        backdrop.setColor(Color.WHITE);
        backdrop.sendBackward();
    }

    /* Determines if a mine has been flagged */
    public boolean flaggedMine() {

        return (flagPresent && minePresent);
    }

    /* Creates the illusion of an exploded mine (red backdrop)
    and informs state of cell that mine has been exploded */
    private void explodeMine() {

        FilledRect explodedRect = new FilledRect
            (x+CELL_BUFFER, y+CELL_BUFFER, CELL_SIZE_ADJUSTED, CELL_SIZE_ADJUSTED, canvas);

        explodedRect.setColor(Color.RED);

        //Reveal the hidden mine
        mine.sendToFront();
        mine.show();

        //Update state of cell to exploded
        exploded = true;

    }

    /* Checks if mine has been exploded (for game over procedures) */
    public boolean isExploded() {

        return exploded;
    }

    /* Shows mine, given that cell contains one, and sends the mine display to the
    very front of canvas (to prevent it from being hidden behind another layer) */
    public void showMine() {

        if (minePresent) {
            mine.show();
            mine.sendToFront();
        }
    }

    /* Keeps track of and updates the status of the cell with each LEFT click*/
    public void handleLeftClick() {

        //If cell has already been flagged, ignore any normal/left clicks on cell
        if(flagPresent)
            return;

        //If a non-mine cell is clicked on, create white backdrop and show neighbor count
        if (!minePresent) {
            createWhiteBackDrop();

            //Update status of cell to --> content of cell has been revealed
            countRevealed = true;

            numText.show();
            numText.sendToFront();
        }
        //Else, player has clicked on a mine, so explode it
        else {
            explodeMine();
        }
    }

    /* Keeps track of and updates the status of the cell with each RIGHT click*/
    public void handleRightClick() {

        //If there is no flag on cell, create a flag and hide it
        if (flagRect == null) {
            createFlagRect();
            flagRect.hide();
        }

        //If player right-clicks again on a flag, remove flag (aka hide it)
        if (flagPresent) {
            flagPresent = false;
            flagRect.hide();
        }
        //Else show flag on another right click
        else {
            flagRect.show();
            flagPresent = true;
        }
    }

}
