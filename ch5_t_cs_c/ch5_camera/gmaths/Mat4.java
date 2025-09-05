package gmaths;

public class Mat4 {   // row column formulation

  private float[][] values;
  
  public Mat4() {
    this(0);
  }
  
  public Mat4(float f) {
    values = new float[4][4];
    makeZero();
    for (int i=0; i<4; ++i) {
      values[i][i] = f;
    }
  }
  
  public Mat4(Mat4 m) {
    this.values = new float[4][4];
    for (int i=0; i<4; ++i) {
      for (int j=0; j<4; ++j) {
        this.values[i][j] = m.values[i][j];
      }
    }
  }
  
  public void set(int r, int c, float f) {
    values[r][c] = f;
  }
  
  private void makeZero() {
    for (int i=0; i<4; ++i) {
      for (int j=0; j<4; ++j) {
        values[i][j] = 0;
      }
    }
  }
  
  public void transpose() {
    for (int i=0; i<4; ++i) {
      for (int j=i; j<4; ++j) {
        float t = values[i][j];
        values[i][j] = values[j][i];
        values[j][i] = t;
      }
    }
  }
    
  public static Mat4 transpose(Mat4 m) {
    Mat4 a = new Mat4(m);
    for (int i=0; i<4; ++i) {
      for (int j=i; j<4; ++j) {
        float t = a.values[i][j];
        a.values[i][j] = a.values[j][i];
        a.values[j][i] = t;
      }
    }
    return a;
  }

  public static Mat4 multiply(Mat4 a, Mat4 b) {
    Mat4 result = new Mat4();
    for (int i=0; i<4; ++i) {
      for (int j=0; j<4; ++j) {
        for (int k=0; k<4; ++k) {
          result.values[i][j] += a.values[i][k]*b.values[k][j];
        }
      }
    }
    return result;
  }
  
  public static Vec3 multiply(Mat4 m, Vec3 v) {
    Vec3 result = new Vec3();
    result.x = m.values[0][0]*v.x + m.values[0][1]*v.y
               + m.values[0][2]*v.z;
    result.y = m.values[1][0]*v.x + m.values[1][1]*v.y
               + m.values[1][2]*v.z;
    result.z = m.values[2][0]*v.x + m.values[2][1]*v.y
               + m.values[2][2]*v.z;
    return result;
  }

  public float[] toFloatArrayForGLSL() {  // col by row
    float[] f = new float[16];
    for (int j=0; j<4; ++j) {
      for (int i=0; i<4; ++i) {
        f[j*4+i] = values[i][j];
      }
    }
    return f;
  }
  
  public String asFloatArrayForGLSL() {  // col by row
    String s = "{";
    for (int j=0; j<4; ++j) {
      for (int i=0; i<4; ++i) {
        s += String.format("%.2f",values[i][j]);
        if (!(j==3 && i==3)) s+=",";
      }
    }
    return s;
  }
  
  public String toString() {
    String s = "{";
    for (int i=0; i<4; ++i) {
      s += (i==0) ? "{" : " {";
      for (int j=0; j<4; ++j) {
        s += String.format("%.2f",values[i][j]);  
        if (j<3) s += ", ";
      }
      s += (i==3) ? "}" : "},\n";
    } 
    s += "}";
    return s;
  }
  
} // end of Mat4 class