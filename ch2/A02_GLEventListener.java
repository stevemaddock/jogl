import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class A02_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  
  /* The constructor is not used to initialise anything */
  public A02_GLEventListener() {
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
    gl.glEnable(GL3.GL_DEPTH_TEST);
    gl.glDepthFunc(GL3.GL_LESS);
    initialise(gl);
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
    gl.glUseProgram(shaderProgram);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3); // drawing one triangle
    gl.glBindVertexArray(0);
  }

  // ***************************************************
  /* THE DATA
   */
   
  // one triangle
  private float[] vertices = {
    -0.5f, -0.5f, 0.0f,  // Bottom Left
     0.5f, -0.5f, 0.0f,  // Bottom Right
     0.0f,  0.5f, 0.0f   // Top middle
  };
  
  // ***************************************************
  /* THE BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
    
  private void fillBuffers(GL3 gl) { 
    gl.glGenVertexArrays(1, vertexArrayId, 0);
                                    // Create and bind a Vertex Array Object
    gl.glBindVertexArray(vertexArrayId[0]);
    
    gl.glGenBuffers(1, vertexBufferId, 0);
                                    // Create and bind OpenGL vertex buffer object
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);

    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
                                    // Fill Java FloatBuffer.
                                    // Works only if orginal data stored in a 1D array.
                                    // Otherwise need to transfer data using a loop, e.g.
                                    // for (... loop over vertex data structure...) {
                                    //   fb.put(...each of...vertex x, y, z data...);
                                    // }
                                    // fb.rewind();

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
                                    // Pass Java FloatBuffer data to OpenGL object
    
    // Tell OpenGL to pass the current vertex buffer object to parameter 0 in the shader
    // parameter 1: the vertex attribute index (i.e. location) to configure - location 0 in the 
    //              vertex shader. The shader code below makes the location clear.
    // parameter 2: size of the attribute, in this case 3 values (i.e. an x,y,z value)
    // parameter 3: type of data, in this case float
    // parameter 4: false - do not normalize the data
    // parameter 5: stride - the space between consecutive vertex attributes in a list of vertices.
    //              In this case it is 3*Float.BYTEs, since there are 3 floats to stride over. 
    // parameter 6: offset of data in the buffer. The position data is at the start of the array, so 
    //              the value is 0.
    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3*Float.BYTES, 0);
    gl.glEnableVertexAttribArray(0); // enable the vertex attribute using its location value
    
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    gl.glBindVertexArray(0);
  }

  // ***************************************************
  /* THE SHADER
   */
  
  private String vertexShaderSource = 
    "#version 330 core\n" +         // version 330 should work on most systems
    "\n" +                          // we choose position as the the name 
                                    // for tranferring (x,y,z) position data to the vertex shader
    "layout (location = 0) in vec3 position;\n" +
    "\n" +
    "void main(){\n" +              // gl_Position is the defualt name the GPU pipeline expects
    "  gl_Position = vec4(position.x, position.y, position.z, 1.0f);\n" +
    "}";

  private String fragmentShaderSource = 
    "#version 330 core\n" +         // version 330 should work on most systems
    "\n" +
    "out vec4 fragColor;\n" +        // we choose fragColor as our output name 
                                    // for the colour produced from the fragment shader
    "\n" +
    "void main(){\n" +
    "  fragColor = vec4(0.1f, 0.7f, 0.9f, 1.0f);\n" +
    "}";
    
  private int shaderProgram;

  private void initialiseShader(GL3 gl) {
                                    // In later programs, read the shaders from text files.
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