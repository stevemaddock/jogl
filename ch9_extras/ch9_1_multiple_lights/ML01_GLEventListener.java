import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class ML01_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public ML01_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,8f,15f));
    this.camera.setTarget(new Vec3(0f,5f,0f));
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
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);    // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    Mat4 p = Mat4Transform.perspective(45, aspect);
    camera.setPerspectiveMatrix(p);
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
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Model[] cube;
  private Light[] lights;

  // textures
  private TextureLibrary textures;

  public void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "diffuse_container", "assets/textures/container2.jpg");
    textures.add(gl, "specular_container", "assets/textures/container2_specular.jpg");
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "cloud", "assets/textures/cloud.jpg");
    textures.add(gl, "matrix", "assets/textures/matrix.jpg");
    textures.add(gl, "black1x1", "assets/textures/black1x1.jpg");
    textures.add(gl, "white1x1", "assets/textures/white1x1.jpg");

    lights = new Light[3];

    lights[0] = new Light(gl, camera);
    lights[0].setMaterial(MaterialConstants.gold.clone());

    lights[1] = new Light(gl, camera);
    lights[1].setMaterial(MaterialConstants.brightWhiteLightSource.clone());

    lights[2] = new Light(gl, camera);
    lights[2].setMaterial(MaterialConstants.cyanPlastic.clone());

    cube = new Model[3];
    cube[0] = makeCube(gl, Mat4.multiply(Mat4Transform.translate(0,4,0), Mat4Transform.scale(4,4,4)),
                       "assets/shaders/fs_standard_dse_ml.txt", 
                       textures.get("diffuse_container"), 
                       textures.get("specular_container"), 
                       textures.get("matrix"));
    cube[1] = makeCube(gl, Mat4.multiply(Mat4Transform.translate(0,4,0), Mat4Transform.scale(2,6,2)),
                       "assets/shaders/fs_standard_dse_ml.txt", 
                       textures.get("diffuse_container"), 
                       textures.get("specular_container"), 
                       textures.get("matrix"));
    cube[2] = makeCube(gl, Mat4.multiply(Mat4Transform.translate(0,4,0), Mat4Transform.scale(6,2,6)),
                       "assets/shaders/fs_standard_dse_ml.txt", 
                       textures.get("diffuse_container"), 
                       textures.get("specular_container"), 
                       textures.get("matrix"));
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    lights[0].setPosition(getLight0Position()); // changing light position each frame
    lights[1].setPosition(getLight1Position()); // changing light position each frame
    lights[2].setPosition(getLight2Position()); // changing light position each frame

    lights[0].render(gl);
    lights[1].render(gl);
    lights[2].render(gl);
    
    cube[0].render(gl);
    cube[1].render(gl);
    cube[2].render(gl);
  }
  

  // **********************************
  /* Cube
   */

  private Model makeCube(GL3 gl, Mat4 m, String fragmentPath, Texture diffuse, Texture specular, Texture emission) {
    String name = "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = m;
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", fragmentPath);
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(specular);
    material.setEmissionMap(emission);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, lights, camera);
  }

  // **********************************
  /* Light
   */

  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLight0Position() {
    double elapsedTime = getSeconds()-startTime;
    float x = 8.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 0.4f;
    float z = 8.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  private Vec3 getLight1Position() {
    double elapsedTime = getSeconds()-startTime;
    float y = 8.0f*(float)(Math.sin(Math.toRadians(elapsedTime*70)));
    float x = 0f;
    float z = 8.0f*(float)(Math.cos(Math.toRadians(elapsedTime*70)));
    return new Vec3(x,y,z);
  }

  private Vec3 getLight2Position() {
    double elapsedTime = getSeconds()-startTime;
    float x = 6.0f*(float)(Math.sin(Math.toRadians(elapsedTime*30)));
    float y = 2.4f;
    float z = 6.0f*(float)(Math.cos(Math.toRadians(elapsedTime*30)));
    return new Vec3(x,y,z);
  }

  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
    // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }

}