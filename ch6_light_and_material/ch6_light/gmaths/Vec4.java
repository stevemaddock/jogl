package gmaths;

public final class Vec4 {
  public float x;
  public float y;
  public float z;
  public float w;
  
  public Vec4() {
    this(0,0,0,1);
  }
  
  public Vec4(Vec3 v) {
    this(v.x, v.y, v.z, 1);
  }
  
  public Vec4(Vec3 v, float w) {
    this(v.x, v.y, v.z, w);
  }
  
  public Vec4(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
      
  public Vec3 toVec3() {
    return new Vec3(x,y,z);
  }
  
  public String toString() {
    return "("+x+","+y+","+z+","+w+")";
  }
} // end of Vec4 class