package animation;

import java.util.List;

public abstract class Transition<T> {

  public static final int LINEAR = 0;

  protected double startTime = 0.0; // time offset to be added to all times
  protected double[] times;
  protected List<T> values;
  protected int size = 0;
  protected int style = LINEAR; 
  
  // repeats the sequence again and again
  protected boolean againAndAgain;

  protected double getOffsetTime(double t) {
    return startTime+t;
  }

  protected double getFirstTime() {
    return getOffsetTime(times[0]);
  }

  protected double getLastTime() {
    return getOffsetTime(times[size-1]);
  }

  protected boolean inRange(double t) {
    return (t>=getFirstTime() && t<=getLastTime());
  }

  protected T g(double t) {
    //System.out.println("Transition.g");
    if (t<=getFirstTime()) return (T)values.get(0);  // before the start time
    else if (t>=getLastTime()) return (T)values.get(values.size()-1);  // after the end time
    else {  // find the two values that t is between
      int i=0;
      while (getOffsetTime(times[i])<t) i++;
      if (style==LINEAR)
        return linearInterpolation(t,getOffsetTime(times[i-1]),getOffsetTime(times[i]),values.get(i-1),values.get(i));  // linear interpolation
      else
        System.err.println("Transition.g: style not implemented");
        System.exit(0);
        return null;
    }
  }

  protected abstract T linearInterpolation(double t, double t1, double t2, T a, T b);
  
  protected T repeatTime(double t) {
    if (t>=getLastTime()) {
      t = getFirstTime() + t-getLastTime();
      return repeatTime(t);
    }
    else 
      return g(t);
  }

  public T get(double t) {
    if (againAndAgain)
      return repeatTime(t);
    else 
      return g(t);
  }

  public String toString() {
    String s = "Transition: " + System.lineSeparator();
    for (T v: values) {
      s += v.toString() + " " + System.lineSeparator();;
    }
    return s;
  }
}

