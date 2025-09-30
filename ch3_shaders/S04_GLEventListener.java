import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

/**
 * This class implements GLEventListener to handle OpenGL events.
 *
 * @author    Dr Steve Maddock
 * @version   1.1 (26/10/2025)
 */

public class S04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  /* The constructor is not used to initialise anything */
  public S04_GLEventListener() {
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
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  // ***************************************************
  /* USEFUL METHODS
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  private float inRange(double x) {
    x = (x+1)*0.5;
    if (x<0) return 0f;
    else if (x>1) return 1.0f;
    else return (float)x;
  }
  
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  public void initialise(GL3 gl) {
    initialiseShader(gl);
    shaderProgram = compileAndLink(gl);
    fillBuffers(gl);
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    double elapsedTime = getSeconds() - startTime;
    
    replaceVBO_XYZ(gl, 0, (float)Math.sin(elapsedTime), (float)Math.cos(elapsedTime),0);
    replaceVBO_XYZ(gl, 1, (float)Math.sin(elapsedTime)*0.5f, (float)Math.cos(elapsedTime)*0.5f,0);
    replaceVBO_XYZ(gl, 2, (float)Math.cos(elapsedTime*0.5), (float)Math.sin(elapsedTime*0.5),0);
    
    gl.glUseProgram(shaderProgram);
    
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  // ***************************************************
  /* THE DATA
   */
   
  private float[] vertices = {
    -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, // Bottom Left, blue (r=0, g=0, b=1)
     0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Bottom Right, green
     0.0f,  0.5f, 0.0f, 1.0f, 0.0f, 0.0f  // Top middle, red
  };
  
  private int vertexStride = 6;
  private int vertexXYZFloats = 3;
  private int vertexColourFloats = 3;
  
  private int[] indices = {         // Note that we start from 0
      0, 1, 2
  }; 
  
  // ***************************************************
  /* THE BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
                                    // We now use an element buffer
  
  private void replaceVBO_XYZ(GL3 gl, int index, float x, float y, float z) {
    float[] aVertex = {x,y,z};
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(aVertex);
    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, Float.BYTES * index * vertexStride, 
                       Float.BYTES * aVertex.length, fb);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }
  
  private void replaceVBO_RGB(GL3 gl, int index, float x, float y, float z) {
    float[] aVertex = {x,y,z};
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(aVertex);
    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, Float.BYTES * (index * vertexStride + vertexXYZFloats),
                       Float.BYTES * aVertex.length, fb);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }
  
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_DYNAMIC_DRAW);
    
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

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  }

  
  // ***************************************************
  /* THE SHADER
   */
  
  private String vertexShaderSource = 
    "#version 330 core\n" +
    "\n" +
    "layout (location = 0) in vec3 position;\n" +
    "layout (location = 1) in vec3 color;\n" +
    "out vec3 aColor;\n" +
    "\n" +
    "void main() {\n" +
    "  gl_Position = vec4(position, 1.0);\n" +
    "  aColor = color;\n" +
    "}";

  private String fragmentShaderSource = 
    "#version 330 core\n" +
    "in vec3 aColor;\n" +
    "out vec4 fragColor;\n" +
    "\n" +
    "void main() {\n" +
    "  fragColor = vec4(aColor, 1.0f);\n" +
    "}";
    
  private int shaderProgram;

  private void initialiseShader(GL3 gl) {
    if (DISPLAY_SHADERS) {
      System.out.println("***Vertex shader***");
      System.out.println(vertexShaderSource);
      System.out.println("\n***Fragment shader***");
      System.out.println(fragmentShaderSource);
    }
  }
  
  private int compileAndLink(GL3 gl) {
    String[][] sources = new String[1][1];
    sources[0] = new String[]{ vertexShaderSource };
    ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
    boolean compiled = vertexShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile vertex shader: " + sources);
    sources[0] = new String[]{ fragmentShaderSource };
    ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
    compiled = fragmentShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile fragment shader: " + sources);
    ShaderProgram program = new ShaderProgram();
    program.init(gl);
    program.add(vertexShaderCode);
    program.add(fragmentShaderCode);
    program.link(gl, System.out);
    if (!program.validateProgram(gl, System.out))
      System.err.println("[error] Unable to link program");
    return program.program();
  }

}