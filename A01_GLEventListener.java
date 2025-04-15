import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
  
public class A01_GLEventListener implements GLEventListener {
  
  /* The constructor is not used to initialise anything */
  public A01_GLEventListener() {
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
                                    // Retrieve the gl context.
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
                                    // Print some diagnostic info.
                                    // Useful, as it shows something is happening.
    System.err.println("INIT GL IS: " + gl.getClass().getName());
    System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
    System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
    System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
                                    // Set the background colour for the OpenGL context: 0.0f <= {r,g,b,alpha} <= 1.0f.
    gl.glClearDepth(1.0f);          // Required for z buffer work in later examples. Will be explained there.
    gl.glEnable(GL.GL_DEPTH_TEST);  // Required for z buffer work in later examples.
    gl.glDepthFunc(GL.GL_LESS);     // Required for z buffer work in later examples.
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
                                    // Will be added to in later examples.
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);                     // A separate method is used for rendering the scene.
                                    // This reduces the clutter in this method.
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
                                    // Will be added to in later examples.
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);  
                                    // JOGL requires both the background and the depth buffer to be cleared
                                    // Do nothing else for now, so only a blank screen is shown.
  }  
  
}