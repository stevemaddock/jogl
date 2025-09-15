package animation;

public final class MyClock {

  private double startTime;
  private double savedTime;
  private boolean paused;

  public MyClock() {
    startTime = 0;
    savedTime = 0;
    paused = true;
  }

  public double getTime() {
    double t = 0;
    if (paused) {
      t = (savedTime - startTime)/1000.0;
    } 
    else {
      t = (System.currentTimeMillis() - startTime)/1000.0;
    }
    return t;
  }

  public void start() {
    paused = false;
    startTime=System.currentTimeMillis(); 
  }

  // should disable the start button until pause has been pressed.
  // following methods should be in clock class
  public void pause() {
    paused = true;
    savedTime = System.currentTimeMillis();
  }

  public void unPause() {
    paused = false;
    startTime = startTime + System.currentTimeMillis()-savedTime;
  }

}