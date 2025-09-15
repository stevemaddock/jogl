package animation;

import java.util.List;

import gmaths.Mat4;

public class Mat4Transition extends Transition<Mat4> {

  public Mat4Transition(double startTime, double[] times, List<Mat4> values, boolean againAndAgain) {
    this.startTime = startTime;
    this.times = times;
    this.values = values;
    this.againAndAgain = againAndAgain;
    size = times.length;
  }

  protected Mat4 linearInterpolation(double t, double t1, double t2, Mat4 a, Mat4 b) {
    Mat4 temp = new Mat4();
    for (int i=0; i<4; i++) {
      for (int j=0; j<4; j++) {
        temp.set(i,j,(float)(a.get(i,j) + (t-t1)*(b.get(i,j)-a.get(i,j))/(t2-t1)));
      }
    }
    return temp;
  }

}