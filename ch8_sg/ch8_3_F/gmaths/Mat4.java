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

  // See https://www.geometrictools.com/Documentation/LaplaceExpansionTheorem.pdf
  public static Mat4 inverse(Mat4 m) {
    float s0 = m.values[0][0] * m.values[1][1] - m.values[1][0] * m.values[0][1];
    float s1 = m.values[0][0] * m.values[1][2] - m.values[1][0] * m.values[0][2];
    float s2 = m.values[0][0] * m.values[1][3] - m.values[1][0] * m.values[0][3];
    float s3 = m.values[0][1] * m.values[1][2] - m.values[1][1] * m.values[0][2];
    float s4 = m.values[0][1] * m.values[1][3] - m.values[1][1] * m.values[0][3];
    float s5 = m.values[0][2] * m.values[1][3] - m.values[1][2] * m.values[0][3];

    float c5 = m.values[2][2] * m.values[3][3] - m.values[3][2] * m.values[2][3];
    float c4 = m.values[2][1] * m.values[3][3] - m.values[3][1] * m.values[2][3];
    float c3 = m.values[2][1] * m.values[3][2] - m.values[3][1] * m.values[2][2];
    float c2 = m.values[2][0] * m.values[3][3] - m.values[3][0] * m.values[2][3];
    float c1 = m.values[2][0] * m.values[3][2] - m.values[3][0] * m.values[2][2];
    float c0 = m.values[2][0] * m.values[3][1] - m.values[3][0] * m.values[2][1];

    // Should check for 0 determinant
    float invdet = 1.0f / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

    Mat4 b = new Mat4();

    b.values[0][0] = ( m.values[1][1] * c5 - m.values[1][2] * c4 + m.values[1][3] * c3) * invdet;
    b.values[0][1] = (-m.values[0][1] * c5 + m.values[0][2] * c4 - m.values[0][3] * c3) * invdet;
    b.values[0][2] = ( m.values[3][1] * s5 - m.values[3][2] * s4 + m.values[3][3] * s3) * invdet;
    b.values[0][3] = (-m.values[2][1] * s5 + m.values[2][2] * s4 - m.values[2][3] * s3) * invdet;

    b.values[1][0] = (-m.values[1][0] * c5 + m.values[1][2] * c2 - m.values[1][3] * c1) * invdet;
    b.values[1][1] = ( m.values[0][0] * c5 - m.values[0][2] * c2 + m.values[0][3] * c1) * invdet;
    b.values[1][2] = (-m.values[3][0] * s5 + m.values[3][2] * s2 - m.values[3][3] * s1) * invdet;
    b.values[1][3] = ( m.values[2][0] * s5 - m.values[2][2] * s2 + m.values[2][3] * s1) * invdet;

    b.values[2][0] = ( m.values[1][0] * c4 - m.values[1][1] * c2 + m.values[1][3] * c0) * invdet;
    b.values[2][1] = (-m.values[0][0] * c4 + m.values[0][1] * c2 - m.values[0][3] * c0) * invdet;
    b.values[2][2] = ( m.values[3][0] * s4 - m.values[3][1] * s2 + m.values[3][3] * s0) * invdet;
    b.values[2][3] = (-m.values[2][0] * s4 + m.values[2][1] * s2 - m.values[2][3] * s0) * invdet;

    b.values[3][0] = (-m.values[1][0] * c3 + m.values[1][1] * c1 - m.values[1][2] * c0) * invdet;
    b.values[3][1] = ( m.values[0][0] * c3 - m.values[0][1] * c1 + m.values[0][2] * c0) * invdet;
    b.values[3][2] = (-m.values[3][0] * s3 + m.values[3][1] * s1 - m.values[3][2] * s0) * invdet;
    b.values[3][3] = ( m.values[2][0] * s3 - m.values[2][1] * s1 + m.values[2][2] * s0) * invdet;

    return b; 
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