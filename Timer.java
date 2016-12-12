import objectdraw.*;
/**
 * Class Timer is an extention of active object. This class is responsible for starting a timer
 * and formatting the time elapsed into a minute:second format (00:00).
 *
 * @author Sabirah Shuaybi
 * @version 12/8/16
 */
public class Timer extends ActiveObject
{

  private MineSweeper mineSweeper;
  private long startTime;
  private boolean gameOver = false;

  //Delay time for animation in seconds
  private static final int PAUSE_TIME_SECONDS = 1;

  public Timer(MineSweeper mineSweeper) {

      //Save reference to MineSweeper class so can update its JLabel each second
      this.mineSweeper = mineSweeper;

      //Compute start time when timer object is constructed
      startTime = System.currentTimeMillis();
  }

  /* Renders state as gameOver */
  public void gameIsOver() {

    gameOver = true;
  }

  /* Responsible for defining various components of the animation such as pause time */
    public void run() {

      //Keep updating timer while game is in progress
        //If game is over (lost/won) exit loop/stop the timer
      while (gameOver == false) {

        //Update timer count every second
        pause(PAUSE_TIME_SECONDS * 1000);

        //Compute current time
        long currentTime = System.currentTimeMillis();

        //Compute the time that has elapsed
        long elapsedTime = currentTime - startTime;

        //Convert elapsed time into seconds
        long totalElapsedSeconds = elapsedTime / 1000;

        //Convert the total elapsed seconds into minutes
        long elapsedMinutes = totalElapsedSeconds / 60;

        //What ever is left over from the conversion to total seconds into minutes
            //(aka the remainder), is the elapsed seconds
        long elapsedSeconds = totalElapsedSeconds % 60;

        //Configure this elapsed time format (minutes:seconds) into a string
        String elapsedTimeString =
            convertToString(elapsedMinutes) + ":" + convertToString(elapsedSeconds);

        //Pass this string to MineSweeper class so it can keep
            //updating its JLabel as game is in progress
        mineSweeper.updateElapsedTime(elapsedTimeString);
      }
    }

    /* Converts the elapsed time (type long) to a string object */
    private String convertToString(long n) {

      //Ensure that the minutes and seconds are displayed as double digits --> 00:00

      if (n < 10) {
        return "0" + n;
      }
      return n + "";
    }
}
