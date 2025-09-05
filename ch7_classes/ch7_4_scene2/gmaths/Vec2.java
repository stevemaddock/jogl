package gmaths;

/**
 * A class for a 2D vector.
 * This includes two components: x and y. 
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (01/10/2017)
 */
 
public final class Vec2 {
  public float x;
  public float y;

  /**
   * Constructor.
   */ 
  public Vec2() {
    this(0,0);
  }

  /**
   * Constructor.
   * @param x x value for the 2D vector.
   * @param y y value for the 2D vector.
   */
  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructor.
   * @param v 2D vector used to initialise the values of this vector.
   */
  public Vec2(Vec2 v) {
    x = v.x;
    y = v.y;
  }

  /**
   * Calculates and returns the length of the vector.
   * 
   * @return  The length (= magnitude) of the vector.
   */ 
  public float length() {
    return magnitude();
  }
  
  /**
   * Calculates and returns the magnitude of the vector.
   * 
   * @return  The magnitude (= length) of the vector.
   */
  public float magnitude() {
    return magnitude(this);
  }

  /**
   * Calculates and returns the magnitude of the vector.
   * 
   * @param v The 2D vector to calculate teh magnitude of.
   * @return  The magnitude (= length) of the supplied vector.
   */
  public static float magnitude(Vec2 v) {
    return (float)Math.sqrt(v.x*v.x+v.y*v.y);
  }

  /**
   * Normalizes the vector. The vector is updated accordingly.
   */
  public void normalize() {
    float mag = magnitude();   // fails if mag = 0
    x /= mag;
    y /= mag;
  }

  /**
   * Normalizes the vector that is supplied a parameter and returns the new normalized vector as the result.
   * @param v The 2D vector to normalize.
   * @return  The normalized version of vector v
   */
  public static Vec2 normalize(Vec2 v) {
    float mag = magnitude(v);   // fails if mag = 0
    return new Vec2(v.x/mag, v.y/mag);
  }

  /**
   * Adds the supplied vector v to this vector and stores the result in this vector.
   * @param v The 2D vector to be added.
   */
  public void add(Vec2 v) {
    x += v.x;   
    y += v.y;
  }

  /**
   * Adds the supplied vectors (a+b) and returns the resulting vector.
   * @param a The first 2D vector
   * @param b The second 2D vector, which is added to the first vector.
   * @return  The result of a+b
   */
  public static Vec2 add(Vec2 a, Vec2 b) {
   return new Vec2(a.x+b.x, a.y+b.y);
  }

  /**
   * Subtracts the supplied vector v from this vector and stores the result in this vector
   * @param v The 2D vector to be subtracted.
   */
  public void subtract(Vec2 v) {
    x -= v.x;   
    y -= v.y;
  }

  /**
   * Subtracts the supplied vectors (a-b) and returns the resulting vector.
   * @param a The first 2D vector
   * @param b The second 2D vector, which is subtracted from the first vector.
   * @return  The result of a-b
   */
  public static Vec2 subtract(Vec2 a, Vec2 b) {
    return new Vec2(a.x-b.x, a.y-b.y);
  }  

  /**
   * Calculates the dot product between this vector and the supplied vector
   * @param v The vector to be used in the dot product.
   * @return  The result of the dot product.
   */
  public float dotProduct(Vec2 v) {
    return dotProduct(this, v);
  }

  /**
   * Calculates the dot product between vectors a and b and returns the result.
   * @param a A 2D vector to be used in the dot product.
   * @param b A 2D vector to be used in the dot product.
   * @return  The result of the dot product.
   */
  public static float dotProduct(Vec2 a, Vec2 b) {
    return a.x*b.x + a.y*b.y;
  }

  /**
   * Each component of this vector is multiplied by f.
   * @param f The floating point value to multiple this vector by.
   */
  public void multiply(float f) {
    x*=f;
    y*=f;
  }

  /**
   * Each component of the vector v is multiplied by f.
   * @param v The vector.
   * @param f The floating point value to multiple the vector by.
   * @return  The result: (v.x*f, v.y*f).
   */
  public static Vec2 multiply(Vec2 v, float f) {
    return new Vec2(v.x*f, v.y*f);
  }

  /**
   * Creates a String from the vector's components.
   * @return  A String representing the vector.
   */
  public String toString() {
    return "("+x+","+y+")";
  }
} // end of Vec2 class