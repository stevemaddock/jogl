import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

/**
 * This class handles OpenGL events for the A03 application.
 *
 * @author    Dr Steve Maddock
 * @version   1.1 (26/10/2025)
 */

public class A03_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  /* The constructor is not used to initialise anything in this example. */
  public A03_GLEventListener() {
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    setUpGLDefaults(gl);
    initialise(gl);
  }
  
  private void setUpGLDefaults(GL3 gl) {
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    // gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);  // draw wireframe
    // gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);  // default: fill triangles
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
                                    // JOGL requires that both the colour buffer and the depth    
                                    // buffer are cleared.
    gl.glUseProgram(shaderProgram); // Choose the shader program to use.
    gl.glBindVertexArray(vertexArrayId[0]);
                                    // Bind the relevant vertex array containing the collection of 
                                    // triangles.
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
                                    // mode, count, type, indices_buffer_offset
                                    // int, int, int, long
                                    // Draw the collection of triangles.
                                    // count is the number of indices used to describe the triangles.
    gl.glBindVertexArray(0);        // Unbind the vertex array.
                                    // A new shader program could now be used and a new vertex array
                                    // object bound to draw a second object or the same vertex array
                                    // could be used to draw the same object again.
  }

  // ***************************************************
  /* THE DATA
   */
  
  // Two triangles to make a rectangle.
    
  private float[] vertices = {      // x,y,z,
      -0.5f,  0.5f, 0.0f,           // Top Left 
      -0.5f, -0.5f, 0.0f,           // Bottom Left
       0.5f, -0.5f, 0.0f,           // Bottom Right
       0.5f,  0.5f, 0.0f           // Top Right
  };  
  
  private int[] indices = {         // Note that we start from 0
      0, 1, 2,                      // First Triangle
      0, 2, 3                       // Second Triangle
  }; 
  
  // ***************************************************
  /* THE BUFFERS
     Create memory buffers on the GPU and transfer the relevant data from the cPU to the GPU.
     Data to be transferred: vertices and index data to form triangles from the vertices.
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
                                    // We now use an element buffer
    
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
                                    // Create and bind a Vertex Array Object.
                                    // This will be the 'container' for the vertex data and the 
                                    // index data.
    gl.glBindVertexArray(vertexArrayId[0]);
    
    gl.glGenBuffers(1, vertexBufferId, 0);
                                    // Create and bind OpenGL vertex buffer object.
                                    // This is the buffer where the vertex data is stored.
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);

    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
                                    // Fill Java FloatBuffer with the vertex data.
                                    // Works only if original data stored in a 1D array.

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
                                    // Pass Java FloatBuffer data to OpenGL object
                                    // The vertex data is passed to the GPU.

    int stride = 3;                 // This is the number of values for each vertex.
                                    // In this case it is 3 because there is an x,y,z 
                                    // value for each vertex.
    int numVertexFloats = 3;        // There are 3 floats for each vertex.
    int offset = 0;                 // We start at position 0 in the vertex list.
    gl.glVertexAttribPointer(0, numVertexFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
                                    // index, size, type, normalized, stride, pointer
                                    // We are using parameter 0 in the shader.
    gl.glEnableVertexAttribArray(0);// Enable the vertex attribute array at index 0

    gl.glGenBuffers(1, elementBufferId, 0);
                                    // Create an element buffer object.
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
                                    // Prepare the indices for transfer to the GPU.
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
                                    // Bind the elemembt buffer object.
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
                                    // Transfer the vertex indices data, i.e. the triangle 
                                    // description data, to the GPU.
    gl.glBindVertexArray(0);        // The vertex array object can now be unbound.
                                    // The vertex data and the indices data are contained in the 
                                    // vertex array object.
                                    // In the render method the vertex array object will be bound, 
                                    // and thus the vertex data and the indices data will then be
                                    // used for drawing the collection of triangles.
  }

  
  // ***************************************************
  /* THE SHADER
     These run on the GPU. The source code is loaded, compiled and transferred to the GPU.
     On the GPU, multiple instances of the vertex shader are spawned and applied to each vertex in parallel. The vertex data will be in a buffer on the GPU.
     Output from the vertex shaders is automatically fed to the next stage on the GPU where triangles are bilinearly interpolated to produce fragments (screen pixels). 
     Multiple instances of the fragment shader are spawned and applied to each fragment in parallel.
     The outputs of the fragment shader are sent to the screen display memory.
   */
  
  private String vertexShaderSource = 
    "#version 330 core\n" +
    "\n" +
    "layout (location = 0) in vec3 position;\n" +
    "\n" +
    "void main() {\n" +
    "  gl_Position = vec4(position.x, position.y, position.z, 1.0);\n" +
    "}";

  private String fragmentShaderSource = 
    "#version 330 core\n" +
    "\n" +
    "out vec4 fragColor;\n" +
    "\n" +
    "void main() {\n" +
    "  fragColor = vec4(0.1f, 0.7f, 0.9f, 1.0f);\n" +
    "}";
    
  private int shaderProgram;

  private void initialiseShader(GL3 gl) {
    if (DISPLAY_SHADERS) {
      System.out.println("***Vertex shader***");
      System.out.println(vertexShaderSource);
      System.out.println("\n***Fragment shader***");
      System.out.println(fragmentShaderSource);
                                    // Display the shaders for diagnostic purposes.
                                    // Check that the shader code looks correct.
                                    // Will be more useful when the text is loaded from file.
    }
  }
  
  private int compileAndLink(GL3 gl) {
                                    // Use JOGL classes to set up the shaders
                                    // We can treat this code as boilerplate code, 
                                    // as it will remain the same in future programs.
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