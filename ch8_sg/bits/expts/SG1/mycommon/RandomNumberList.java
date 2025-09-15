package mycommon;

public class RandomNumberList {

  public static final int MAX_SIZE = 1000;

  private double[] randoms;

  public RandomNumberList() {
    this(MAX_SIZE);
  }

  public RandomNumberList(int size) {
    createRandomNumbers(size);
  }

  private void createRandomNumbers(int size) {
    randoms = new double[size];
    for (int i=0; i<size; ++i) {
      randoms[i] = Math.random();
    }
  }
}