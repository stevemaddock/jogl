package mesh;

public final class Sphere {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
 
  private static final int X_LONG = 30;
  private static final int Y_LAT = 30;
  
  public static final float[] vertices = createVertices();
  public static final int[] indices = createIndices();

  private static float[] createVertices() {
    double r = 0.5;
    int step = 8;
    //float[] 
    float[] vertices = new float[X_LONG*Y_LAT*step];
    for (int j = 0; j<Y_LAT; ++j) {
      double b = Math.toRadians(-90+180*(double)(j)/(Y_LAT-1));
      for (int i = 0; i<X_LONG; ++i) {
        double a = Math.toRadians(360*(double)(i)/(X_LONG-1));
        double z = Math.cos(b) * Math.cos(a);
        double x = Math.cos(b) * Math.sin(a);
        double y = Math.sin(b);
        int base = j*X_LONG*step;
        vertices[base + i*step+0] = (float)(r*x);
        vertices[base + i*step+1] = (float)(r*y);
        vertices[base + i*step+2] = (float)(r*z); 
        vertices[base + i*step+3] = (float)x;
        vertices[base + i*step+4] = (float)y;
        vertices[base + i*step+5] = (float)z;
        vertices[base + i*step+6] = (float)(i)/(float)(X_LONG-1);
        vertices[base + i*step+7] = (float)(j)/(float)(Y_LAT-1);
      }
    }
    return vertices;
    
    //debugging code:
    //for (int i=0; i<vertices.length; i+=step) {
    //  System.out.println(vertices[i]+", "+vertices[i+1]+", "+vertices[i+2]);
    //}
  }
  
  private static int[] createIndices() {
    int[] indices = new int[(X_LONG-1)*(Y_LAT-1)*6];
    for (int j = 0; j<Y_LAT-1; ++j) {
      for (int i = 0; i<X_LONG-1; ++i) {
        int base = j*(X_LONG-1)*6;
        indices[base + i*6+0] = j*X_LONG+i;
        indices[base + i*6+1] = j*X_LONG+i+1;
        indices[base + i*6+2] = (j+1)*X_LONG+i+1;
        indices[base + i*6+3] = j*X_LONG+i;
        indices[base + i*6+4] = (j+1)*X_LONG+i+1;
        indices[base + i*6+5] = (j+1)*X_LONG+i;
      }
    }
    return indices;
    
    //debugging code:
    //for (int i=0; i<indices.length; i+=3) {
    //  System.out.println(indices[i]+", "+indices[i+1]+", "+indices[i+2]);
    //}
  }
  
}