package mycommon;

import gmaths.*;
import mesh.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
  
public class Light { 

  private Material material;
  private Vec3 position;
  private Shader shader;
  private CameraLibrary cameras;
  //private Mat4 perspective;
    
  public Light(GL3 gl) {
    material = MaterialConstants.dullWhiteLightSource;
    position = new Vec3(3f,2f,1f);
    shader = new Shader(gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
    fillBuffers(gl);
  }
  
  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }
  
  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }
  
  public Vec3 getPosition() {
    return position;
  }
  
  public void setMaterial(Material m) {
    material = m;
  }
  
  public Material getMaterial() {
    return material;
  }
  
  public void setCameraLibrary(CameraLibrary cameras) {
    this.cameras = cameras;
  }
  
  public void render(GL3 gl) { //, Mat4 perspective, Mat4 view) {
    //System.out.println("Light.render");
    Mat4 mm = new Mat4(1);
    mm = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), mm);
    mm = Mat4.multiply(Mat4Transform.translate(position), mm);
    
    Mat4 mvpMatrix = Mat4.multiply(cameras.getCurrentCamera().getPerspectiveMatrix(), 
                                   Mat4.multiply(cameras.getCurrentCamera().getViewMatrix(),
                                                 mm));
    
    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

    // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  
  private float[] vertices = CubeXYZOnly.vertices;
  private int[] indices =  CubeXYZOnly.indices;
    
  private int vertexStride = 3;
  private int vertexXYZFloats = 3;
  
  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
    
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
     
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    //gl.glBindVertexArray(0);
  } 

  private int frameNum = 0;

  public void update() {
    frameNum++;
    float lightMultiplier = 9f/6f;
    float x = 8.0f*(float)(Math.sin(Math.toRadians(frameNum*lightMultiplier)));
    float y = 0.4f;
    float z = 8.0f*(float)(Math.cos(Math.toRadians(frameNum*lightMultiplier)));
    setPosition(new Vec3(x,y,z));
  }

}