package gmaths;

/**
 * A static class for a set of methods to create transformation matrices.
 * The matrix is represented in row-column form.
 * Matrices are formed so as to be used in premultiplication of vectors, i.e. matrix <strong>x</strong> vector.
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (01/10/2017)
 */
 
public final class Mat4Transform  {   // row column formulation
  
  private final static float DEFAULT_NEAR_CLIP = 0.1f;
  private final static float DEFAULT_FAR_CLIP  = 100.0f;

  /**
   * Creates a 4x4 translation matrix
   * 
   * @param v The translation amount (x,y,z)
   * @return  The resulting 4x4 translation matrix, as a Mat4
   */
  public static Mat4 translate(Vec3 v) {
    return translate(v.x, v.y, v.z);
  }
  
  /**
   * Creates a 4x4 translation matrix
   * 
   * @param tx The translation amount for x
   * @param ty The translation amount for y
   * @param tz The translation amount for z
   * @return  The resulting 4x4 translation matrix, as a Mat4
   */
   public static Mat4 translate(float tx, float ty, float tz) {
    Mat4 m = new Mat4(1);
    m.set(0,3, tx);
    m.set(1,3, ty);
    m.set(2,3, tz);
    return m;
  }
  
  /**
   * Creates a 4x4 scale matrix
   * 
   * @param v The scale amount (x,y,z)
   * @return  The resulting 4x4 scale matrix, as a Mat4
   */
   public static Mat4 scale(Vec3 v) {
    return scale(v.x, v.y, v.z);
  }
  
  /**
   * Creates a 4x4 scale matrix
   * 
   * @param sx The scale amount for x
   * @param sy The scale amount for y
   * @param sz The scale amount for z
   * @return  The resulting 4x4 scale matrix, as a Mat4
   */
   public static Mat4 scale(float sx, float sy, float sz) {
    Mat4 m = new Mat4(1);
    m.set(0,0, sx);
    m.set(1,1, sy);
    m.set(2,2, sz);
    return m;
  }

  /**
   * Creates a 4x4 rotation matrix to rotate around the X axis.
   * 
   * @param angle The amount of rotation in degrees. Positive values indicate anticlockwise rotation.
   * @return  The resulting 4x4 rotation matrix, as a Mat4
   */
   public static Mat4 rotateAroundX(float angle) {   // angle in degrees
    Mat4 m = new Mat4(1);
    angle = (float)(angle*Math.PI/180.0);
    m.set(1,1, (float)Math.cos(angle));
    m.set(1,2, -(float)Math.sin(angle));
    m.set(2,1, (float)Math.sin(angle));
    m.set(2,2, (float)Math.cos(angle));
    return m;
  }
  
  /**
   * Creates a 4x4 rotation matrix to rotate around the Y axis.
   * 
   * @param angle The amount of rotation in degrees. Positive values indicate anticlockwise rotation.
   * @return  The resulting 4x4 rotation matrix, as a Mat4
   */
   public static Mat4 rotateAroundY(float angle) {   // angle in degrees
    Mat4 m = new Mat4(1);
    angle = (float)(angle*Math.PI/180.0);
    m.set(0,0, (float)Math.cos(angle));
    m.set(0,2, (float)Math.sin(angle));
    m.set(2,0, -(float)Math.sin(angle));
    m.set(2,2, (float)Math.cos(angle));
    return m;
  }

  /**
   * Creates a 4x4 rotation matrix to rotate around the Z axis.
   * 
   * @param angle The amount of rotation in degrees. Positive values indicate anticlockwise rotation.
   * @return  The resulting 4x4 rotation matrix, as a Mat4
   */
   public static Mat4 rotateAroundZ(float angle) {   // angle in degrees
    Mat4 m = new Mat4(1);
    angle = (float)(angle*Math.PI/180.0);
    m.set(0,0, (float)Math.cos(angle));
    m.set(0,1, -(float)Math.sin(angle));
    m.set(1,0, (float)Math.sin(angle));
    m.set(1,1, (float)Math.cos(angle));
    return m;
  }
  
  /**
   * Creates a perspective matrix with near clip plane at 0.1f and far clip plane at 100f.
   * 
   * @param fov The field of view for the perspective. Default is 45.
   * @param aspect The aspect ratio of the display area, which equals width/height, where width and height are floating point values.
   * @return  The resulting perspective matrix, as a Mat4
   */
   public static Mat4 perspective(float fov, float aspect) {
    return perspective(fov, aspect, DEFAULT_NEAR_CLIP, DEFAULT_FAR_CLIP);
  }
  
  /**
   * Creates a perspective matrix.
   * 
   * @param fov The field of view for the perspective. Default is 45f.
   * @param aspect The aspect ratio of the display area, which equals width/height, where width and height are floating point values.
   * @param near The distance of the near clip plane. Default is 0.1f;
   * @param far The distance of the far clip plane. Default is 100f.
   * @return  The resulting perspective matrix, as a Mat4
   */
   public static Mat4 perspective(float fov, float aspect, float near, float far) {
    float field = (float)Math.tan(Math.toRadians(fov*0.5f)); 
    float sx = 1/(field*aspect);
    float sy = 1/field;
    float sz = -(far+near)/(far-near);
    float pz = -(2*far*near)/(far-near);
    Mat4 p = new Mat4(0);
    p.set(0, 0, sx);
    p.set(1, 1, sy);
    p.set(2, 2, sz);
    p.set(3, 2, -1);
    p.set(2, 3, pz);
    return p;
  }
  
  /**
   * Creates a view matrix corresponding to a camera at a given position, looking at a particular target point, with a specified worldup direction.
   * The worldup direction will be changed using the Gram-Schmidt process to be perpendicular to the vector joining the position to the target.
   * 
   * @param from The camera postion.
   * @param to The target that the camera is looking at.
   * @param worldup The up direction for the world. 
   * @return  The 4x4 viewing matrix, as a Mat4.
   */
   public static Mat4 lookAt(Vec3 from, Vec3 to, Vec3 worldup) {
    Vec3 front = Vec3.subtract(to, from);
    front.normalize();
    Vec3 right = Vec3.crossProduct(front, worldup);
    right.normalize();
    Vec3 up = Vec3.crossProduct(right, front);
    up.normalize();
    Mat4 view = new Mat4(1);
    view.set(0, 0, right.x);
    view.set(0, 1, right.y);
    view.set(0, 2, right.z);
    view.set(1, 0, up.x);
    view.set(1, 1, up.y);
    view.set(1, 2, up.z);
    view.set(2, 0, -front.x);
    view.set(2, 1, -front.y);
    view.set(2, 2, -front.z);
    Mat4 cam = new Mat4(1);
    cam.set(0, 3, -from.x);
    cam.set(1, 3, -from.y);
    cam.set(2, 3, -from.z);
    Mat4 result = Mat4.multiply(view, cam);
    return result;
  }
  
} // end of class