import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class L02_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public L02_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(-10,6,20));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    //gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    //gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    //gl.glCullFace(GL.GL_BACK);    // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    // GL3 gl = drawable.getGL().getGL3();
    // gl.glDeleteBuffers(1, vertexBufferId, 0);
    // gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    // gl.glDeleteBuffers(1, elementBufferId, 0);
    // gl.glDeleteBuffers(1, light_vertexBufferId, 0);
    // gl.glDeleteVertexArrays(1, light_vertexArrayId, 0);
    // gl.glDeleteBuffers(1, light_elementBufferId, 0);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  public void initialise(GL3 gl) {
    shaderCube = new Shader(gl, "assets/shaders/vs_cube_01.txt", "assets/shaders/fs_cube_01_specular.txt");
    shaderLight = new Shader(gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
    fillBuffers(gl);
    light_fillBuffers(gl);
  }
  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    Mat4 projectionMatrix = camera.getPerspectiveMatrix();
    Mat4 viewMatrix = camera.getViewMatrix();
    //Mat4 lightModelMatrix = getLightModelMatrix();  // side effect: changes global light position
    Mat4 lightModelMatrix = getMovingLightModelMatrix(); // side effect: changes global light position
    renderLight(gl, shaderLight, lightModelMatrix, viewMatrix, projectionMatrix);
    // renderCube requires global light position
    renderCube(gl, shaderCube, getCubeModelMatrix(), viewMatrix, projectionMatrix);
  }
  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  // **********************************
  /* Rendering the cube
   */
   
  private Mat4 getCubeModelMatrix() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    return modelMatrix;
  }
  
  private void renderCube(GL3 gl, Shader shader, Mat4 modelMatrix, Mat4 viewMatrix, Mat4 projectionMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(projectionMatrix, Mat4.multiply(viewMatrix, modelMatrix));
    
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "view", viewMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "projection", projectionMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    Mat4 m = Mat4.inverse(modelMatrix);
    m.transpose();
    shader.setFloatArray(gl, "tiModel", m.toFloatArrayForGLSL());

    shader.setFloat(gl, "objectColor", 1.0f, 0.5f, 0.31f);
    shader.setFloat(gl, "lightColor", 1f,1f,1f);
    shader.setVec3(gl, "lightPos", lightPosition);
    shader.setVec3(gl, "viewPos", camera.getPosition());
  
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  // **********************************
  /* Rendering the light as an object
   */

  private Vec3 lightPosition = new Vec3(4f,5f,8f);
  
  private Mat4 getLightModelMatrix() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(lightPosition), modelMatrix);
    return modelMatrix;
  }
  
  // Alternative version for moving light
   private Mat4 getMovingLightModelMatrix() {
    double elapsedTime = getSeconds()-startTime;
    lightPosition.x = 7.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    lightPosition.y = 4.0f;
    lightPosition.z = 7.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(lightPosition), modelMatrix);
    return modelMatrix;
  }
  
  private void renderLight(GL3 gl, Shader shader, Mat4 modelMatrix, Mat4 view, Mat4 projection) {
    Mat4 mvpMatrix = Mat4.multiply(projection, Mat4.multiply(view, modelMatrix));
    
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    gl.glBindVertexArray(light_vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, light_indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }


  // ***************************************************
  /* THE DATA for the cube
   */
  // anticlockwise/counterclockwise ordering

   private float[] vertices = new float[] {  // x,y,z, nx,ny,nz, s,t
      -0.5f, -0.5f, -0.5f,  -1f, 0f, 0f,  0.0f, 0.0f,  // 0
      -0.5f, -0.5f,  0.5f,  -1f, 0f, 0f,  1.0f, 0.0f,  // 1
      -0.5f,  0.5f, -0.5f,  -1f, 0f, 0f,  0.0f, 1.0f,  // 2
      -0.5f,  0.5f,  0.5f,  -1f, 0f, 0f,  1.0f, 1.0f,  // 3
       0.5f, -0.5f, -0.5f,   1f, 0f, 0f,  1.0f, 0.0f,  // 4
       0.5f, -0.5f,  0.5f,   1f, 0f, 0f,  0.0f, 0.0f,  // 5
       0.5f,  0.5f, -0.5f,   1f, 0f, 0f,  1.0f, 1.0f,  // 6
       0.5f,  0.5f,  0.5f,   1f, 0f, 0f,  0.0f, 1.0f,  // 7

      -0.5f, -0.5f, -0.5f,  0f,0f,-1f,  1.0f, 0.0f,  // 8
      -0.5f, -0.5f,  0.5f,  0f,0f,1f,   0.0f, 0.0f,  // 9
      -0.5f,  0.5f, -0.5f,  0f,0f,-1f,  1.0f, 1.0f,  // 10
      -0.5f,  0.5f,  0.5f,  0f,0f,1f,   0.0f, 1.0f,  // 11
       0.5f, -0.5f, -0.5f,  0f,0f,-1f,  0.0f, 0.0f,  // 12
       0.5f, -0.5f,  0.5f,  0f,0f,1f,   1.0f, 0.0f,  // 13
       0.5f,  0.5f, -0.5f,  0f,0f,-1f,  0.0f, 1.0f,  // 14
       0.5f,  0.5f,  0.5f,  0f,0f,1f,   1.0f, 1.0f,  // 15

      -0.5f, -0.5f, -0.5f,  0f,-1f,0f,  0.0f, 0.0f,  // 16
      -0.5f, -0.5f,  0.5f,  0f,-1f,0f,  0.0f, 1.0f,  // 17
      -0.5f,  0.5f, -0.5f,  0f,1f,0f,   0.0f, 1.0f,  // 18
      -0.5f,  0.5f,  0.5f,  0f,1f,0f,   0.0f, 0.0f,  // 19
       0.5f, -0.5f, -0.5f,  0f,-1f,0f,  1.0f, 0.0f,  // 20
       0.5f, -0.5f,  0.5f,  0f,-1f,0f,  1.0f, 1.0f,  // 21
       0.5f,  0.5f, -0.5f,  0f,1f,0f,   1.0f, 1.0f,  // 22
       0.5f,  0.5f,  0.5f,  0f,1f,0f,   1.0f, 0.0f   // 23
   };
    
   private int[] indices =  new int[] {
      0,1,3, // x -ve 
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      9,13,15, // z +ve
      15,11,9, // z +ve
      8,10,14, // z -ve
      14,12,8, // z -ve
      16,20,21, // y -ve
      21,17,16, // y -ve
      23,22,18, // y +ve
      18,19,23  // y +ve
  };
  
  private int vertexStride = 8;
  private int vertexXYZFloats = 3;
  private int vertexColourFloats = 3;
  private int vertexTexFloats = 2;
  
  // ***************************************************
  /* THE BUFFERS
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
  
    int numColorFloats = vertexColourFloats; // red, green and blue values for each colour 
    offset = numXYZFloats*Float.BYTES;  // the colour values are three floats after the three x,y,z values
                                    // so change the offset value
    gl.glVertexAttribPointer(1, numColorFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
                                    // the vertex shader uses location 1 (sometimes called index 1)
                                    // for the colour information
                                    // location, size, type, normalize, stride, offset
                                    // offset is relative to the start of the array of data
    gl.glEnableVertexAttribArray(1);// Enable the vertex attribute array at location 1

    // now do the texture coordinates  in vertex attribute 2
    int numTexFloats = vertexTexFloats;
    offset = (numXYZFloats+numColorFloats)*Float.BYTES;
    gl.glVertexAttribPointer(2, numTexFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(2);
    
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    //gl.glBindVertexArray(0);
  }

   //* Repeat for light object - inefficient but will do for now.
  
    // ***************************************************
  /* THE DATA for the light
   */
  // anticlockwise/counterclockwise ordering
  
  private float[] light_vertices = new float[] {  // x,y,z
      -0.5f, -0.5f, -0.5f,  // 0
      -0.5f, -0.5f,  0.5f,  // 1
      -0.5f,  0.5f, -0.5f,  // 2
      -0.5f,  0.5f,  0.5f,  // 3
       0.5f, -0.5f, -0.5f,  // 4
       0.5f, -0.5f,  0.5f,  // 5
       0.5f,  0.5f, -0.5f,  // 6
       0.5f,  0.5f,  0.5f   // 7
   };
    
  private int[] light_indices =  new int[] {
      0,1,3, // x -ve 
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      1,5,7, // z +ve
      7,3,1, // z +ve
      6,4,0, // z -ve
      0,2,6, // z -ve
      0,4,5, // y -ve
      5,1,0, // y -ve
      2,3,7, // y +ve
      7,6,2  // y +ve
  };
    
  private int light_vertexStride = 3;
  private int light_vertexXYZFloats = 3;
  
  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  private int[] light_vertexBufferId = new int[1];
  private int[] light_vertexArrayId = new int[1];
  private int[] light_elementBufferId = new int[1];
    
  private void light_fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, light_vertexArrayId, 0);
    gl.glBindVertexArray(light_vertexArrayId[0]);
    gl.glGenBuffers(1, light_vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, light_vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(light_vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * light_vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = light_vertexStride;
    int numXYZFloats = light_vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
     
    gl.glGenBuffers(1, light_elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(light_indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, light_elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * light_indices.length, ib, GL.GL_STATIC_DRAW);
    //gl.glBindVertexArray(0);
  } 

}