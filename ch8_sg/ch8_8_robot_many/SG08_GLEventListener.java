import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class SG08_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public SG08_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(8f,6f,20f));
    this.camera.setTarget(new Vec3(0f,5,0f));
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

  private Floor floor;

  private Robot[] robot;

  private Light light;

  // textures
  private TextureLibrary textures;

  public void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "container_diffuse", "assets/textures/container2.jpg");
    textures.add(gl, "container_specular", "assets/textures/container2_specular.jpg");
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "cloud", "assets/textures/cloud.jpg");
    textures.add(gl, "matrix", "assets/textures/matrix.jpg");
    textures.add(gl, "black1x1", "assets/textures/black1x1.jpg");
    textures.add(gl, "white1x1", "assets/textures/white1x1.jpg");
    textures.add(gl, "jade_diffuse", "assets/textures/jade.jpg");
    textures.add(gl, "jade_specular", "assets/textures/jade_specular.jpg");
    textures.add(gl, "watt_diffuse", "assets/textures/wattBook.jpg");
    textures.add(gl, "watt_specular", "assets/textures/wattBook_specular.jpg");
    textures.add(gl, "smile", "assets/textures/smile.png");
    textures.add(gl, "frown", "assets/textures/frown.png");

    light = new Light(gl, camera);
    Material material = new Material();
    material.setAmbient(0.3f, 0.3f, 0.3f);
    material.setDiffuse(0.7f, 0.7f, 0.7f);
    material.setSpecular(0.7f, 0.7f, 0.7f);
    light.setMaterial(material);

    floor = new Floor(gl, light, camera, textures.get("chequerboard"));

    robot = new Robot[5];
    for (int i=0; i<robot.length; ++i) {
      robot[i] = new Robot(gl, camera, light, 
                      textures.get("smile"), 
                      textures.get("container_diffuse"), textures.get("container_specular"),
                      textures.get("watt_diffuse"), textures.get("watt_specular"));
    }
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame
    light.render(gl);

    floor.render(gl);
    if (animation) {
      for (Robot r : robot) {
        r.updatePosition(Floor.FLOOR_MIN, Floor.FLOOR_MAX);
      }
    }
    for (Robot r : robot) {
      r.render(gl);
    }
  }


  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
   
  public void startAnimation() {
    animation = true;
  }
   
  public void stopAnimation() {
    animation = false;
  }

  // **********************************
  /* Light
   */

    // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 7.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 5.4f;
    float z = 7.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
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

class Floor {

  public static final float FLOOR_MAX = 8f;
  public static final float FLOOR_MIN = -FLOOR_MAX;

  private Model floor;

  public Floor(GL3 gl, Light light, Camera camera, Texture diffuse) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mat4 modelMatrix = getM1();
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_d.txt");
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(null);
    material.setEmissionMap(null);
    Renderer renderer = new Renderer();
    floor = new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  public void render(GL3 gl) {
    floor.render(gl);
  }

  private Mat4 getM1() {
    float size = FLOOR_MAX*2;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    return modelMatrix;
  }
}