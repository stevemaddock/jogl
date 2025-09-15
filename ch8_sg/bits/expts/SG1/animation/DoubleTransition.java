package animation;

import java.util.List;

public class DoubleTransition extends Transition<Double> {

  public DoubleTransition(double startTime, double[] times, List<Double> values, boolean againAndAgain) {
    this.startTime = startTime;
    this.times = times;
    this.values = values;
    this.againAndAgain = againAndAgain;
    size = times.length;
  }

  protected Double linearInterpolation(double t, double t1, double t2, Double a, Double b) {
    return (Double)a + (t-t1)*((Double)b-(Double)a)/(t2-t1);
  }
  
}