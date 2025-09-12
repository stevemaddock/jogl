package gmaths;

public final class Vec3 {
  public float x;
  public float y;
  public float z;
  
  public Vec3() {
    this(0,0,0);
  }
  
  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vec3(Vec3 v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
  }

  public float length() {
    return magnitude();
  }
    
  public float magnitude() {
    return magnitude(this);
  }
  
  public static float magnitude(Vec3 v) {
    return (float)Math.sqrt(v.x*v.x+v.y*v.y+v.z*v.z);
  }
  
  public void normalize() {
    float mag = magnitude();   // fails if mag = 0
    x /= mag;
    y /= mag;
    z /= mag;
  }
  
  public static Vec3 normalize(Vec3 v) {
    float mag = magnitude(v);   // fails if mag = 0
    return new Vec3(v.x/mag, v.y/mag, v.z/mag);
  }
  
  public void add(Vec3 v) {
    x += v.x;
    y += v.y;
    z += v.z;
  }

  public static Vec3 add(Vec3 a, Vec3 b) {
    return new Vec3(a.x+b.x, a.y+b.y, a.z+b.z);
  }
    
  public void subtract(Vec3 v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
  }
  
  public static Vec3 subtract(Vec3 a, Vec3 b) {
    return new Vec3(a.x-b.x, a.y-b.y, a.z-b.z);
  } 
  
  public float dotProduct(Vec3 v) {
    return dotProduct(this, v);
  }
  
  public static float dotProduct(Vec3 a, Vec3 b) {
    return a.x*b.x + a.y*b.y + a.z*b.z;
  }
  
  public void multiply(float f) {
    x*=f;
    y*=f;
    z*=f;
  }
  
  public static Vec3 multiply(Vec3 v, float f) {
    return new Vec3(v.x*f, v.y*f, v.z*f);
  }
  
  public static Vec3 crossProduct(Vec3 a, Vec3 b) {
    return new Vec3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
  }
 
  public String toString() {
    return "("+x+","+y+","+z+")";
  }
  
} // end of Vec3 class