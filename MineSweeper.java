import objectdraw.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;

/**
 * The main responsibliity of Class MineSweeper is to define event handlers. This includes,
 * distinguishing between a right and left click and delegating these events to the Grid class,
 * which handles them accordingly. MineSweeper also displays and activates the New Game button
 * (swing component) and also displays, formats and updates the "Mines found: __/__" label.
 *
 * @author Sabirah Shuaybi
 * @version 11/29/16
 */

public class MineSweeper extends WindowController implements ActionListener, MouseListener
{
    //All the swing components needed for game display
    private JButton newGame;
    private JButton cheat;
    private JComboBox pickLevel;
    private JLabel minesFound;
    private JLabel timerLabel;

    private Grid grid;
    private Timer timer;

    //To hold information about various levels of difficulty
    private static final int EASY_LEVEL = 10;
    private static final int MEDIUM_LEVEL = 15;
    private static final int HARD_LEVEL = 20;
    private int level = EASY_LEVEL;

    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 790;
    private static final int MSG_FONT_SIZE = 25;

    /* Sets up the game display (via resizing, addition of swing components and grid construction) */
    public void begin() {

        //Resize window to accomodate a larger grid setup (for a bigger, clearer game display)
        resize(WINDOW_WIDTH, WINDOW_HEIGHT);

        setSwingComponents();

        newGame();

        //Start listening to mouse events
        canvas.addMouseListener(this);
    }

    /* Sets up and formats all swing components for game display: JLabels, JComboBox and JButton */
    private void setSwingComponents() {

        //Set up the panel structure for swing components in a vertical (Y) boxed layout
        JPanel componentPanel = new JPanel();
        componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
        JPanel subPanel = new JPanel();
        componentPanel.add(subPanel);

        //Create and center new game button
        newGame = new JButton("New Game");
        newGame.setAlignmentX(Component.CENTER_ALIGNMENT);


        //Create and center "Mines found: __ /__" label depending on level
        minesFound = new JLabel("Mines found: 0 / " + level);
        minesFound.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create menu for different levels
        pickLevel = new JComboBox();

        //Add the menu items aka levels of difficulty to JComboBox
        pickLevel.addItem("Easy");
        pickLevel.addItem("Medium");
        pickLevel.addItem("Hard");


        timerLabel = new JLabel("Elapsed Time: 00:00");

        componentPanel.add(minesFound);

        //Add spacing after Mines found label
            //to prevent it from being displayed on the very bottom edge of window
        componentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        //Add remaining 3 components to mini sub panel
        subPanel.add(newGame);
        subPanel.add(timerLabel);
        subPanel.add(pickLevel);


        //Add the main panel to bottom of window
        add(componentPanel, BorderLayout.SOUTH);

        //Add action listener to components to "listen" for an event aka a mouse click
        //Pass running instance of the MineSweeper class
        newGame.addActionListener(this);
        pickLevel.addActionListener(this);
    }

    /* Handles left and right mouse clicks */
    public void mousePressed(MouseEvent event) {

        //Get the location of the mouse event
        Location point = new Location (event.getX(), event.getY());

        //Get the grid cell that was clicked (by passing in the location of the event)
        GridCell clickedCell = grid.getClickedCellAt(point);

        //If something other than a grid cell was clicked on, ignore click (exit method)
        if (clickedCell == null)
            return;

        //If mouse event is a normal left click, pass clicked cell to grid
            //The grid class will then process this left click and act accordingly
        if (event.getButton() == MouseEvent.BUTTON1) {
            grid.handleLeftClick(clickedCell);
        }
        //Else (mouse event is a right click) pass clicked cell to grid
            //The grid class will then process this right click and act accordingly
        else {
            grid.handleRightClick(clickedCell);

            //Update the Mines found label since a right click on a cell means
                //that a mine has been found (mine flagged)
            updateMinesFoundCount();
        }
        //With each mouse event, ask grid for a potential game status
           //(won or lost) to display message
        displayMessage(grid.getStatusMessage());

        //If game is over, stop timer
        if (grid.isGameOver())
            timer.gameIsOver();
    }

    /* Displays a game status message to user depending on string passed in */
    public void displayMessage(String message) {

        //If there's no message to be displayed, exit method
        if (message == null) return;

        Text msg = new Text(message, 0, 630, canvas);

        //Format the text (bigger and bolder)
        msg.setFontSize(MSG_FONT_SIZE);
        msg.setBold(true);

        //Dynamically center message within canvas
        msg.moveTo(((canvas.getWidth()/2) - (msg.getWidth()/2)), 630);

        //Set message to red if player loses
        if(message == "Mine exploded! You lost!") {
            msg.setColor(Color.RED);
        }
        //Or set to green if player wins
        else
            msg.setColor(Color.GREEN);

    }

    /* Updates the Mine found label to the current number of mines discovered
    out of the total mines present (10, 15, or 20 mines) */
    private void updateMinesFoundCount() {

        //Change numerator to the number of flags present on screen
            //Change denominator to total number of mines present on grid (determined by level)
        minesFound.setText("Mines found: " + grid.countFlags() + " /" + level);
    }

    /* Updates timer label based on string passed in */
    public void updateElapsedTime(String elapsedTimeString) {

        timerLabel.setText("Elapsed Time: " + elapsedTimeString);
    }

    /* Required method by interface, evaluates the source of the event and
    performs the action associated with that component */
    public void actionPerformed (ActionEvent e) {

        //Reset to a new game if the new game button is clicked
        if(e.getSource() == newGame) {
            newGame();
        }

        //If user interacts with the level menu...
        else if(e.getSource() == pickLevel) {

            //Reset the game each time a particular level is selected by invoking newGame(),
                //This will allow user to switch levels mid-game

            //And if user selects easy, update state of game to easy level
            if(pickLevel.getSelectedItem().toString() == "Easy") {
                level = EASY_LEVEL;
                newGame();
            }
            //Or if user selects medium, update state of game to medium level
            else if(pickLevel.getSelectedItem().toString() == "Medium") {
                level = MEDIUM_LEVEL;
                newGame();
            }
            //Or if user selects hard, update state of game to hard level
            else if(pickLevel.getSelectedItem().toString() == "Hard") {
                level = HARD_LEVEL;
                newGame();
            }

        }
    }

    /* Performs all acts needed for a new game/reset */
    private void newGame() {

        //Wipe everything off the canvas
        canvas.clear();

        //Construct a fresh grid object
        grid = new Grid(canvas, level);

        //Set Mines Found: __/__ based on the level passed in
        updateMinesFoundCount();

        //If a timer object already exists, stop existing timer before creating a new one
        if (timer != null) {
            timer.gameIsOver();
        }

        //Pass timer object a reference to MineSweeper class
            //so it can keep updating timer label
        timer = new Timer(this);

        //Start the clock
        timer.start();
    }

    public void mouseClicked(MouseEvent event) {}

    public void mouseReleased(MouseEvent event) {}

    public void mouseEntered(MouseEvent event) {}

    public void mouseExited(MouseEvent event) {}
}
