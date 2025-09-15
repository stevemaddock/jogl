import gmaths.*;
import mycommon.*;
import animation.*;

import com.jogamp.opengl.*;

import java.util.*;
  
public class SG1_GLEventListener implements GLEventListener {
  
  //private static final boolean DISPLAY_SHADERS = false;
  private CameraControl cameraControl;
  private MyClock clock;
    
  /* The constructor is not used to initialise anything */
  public SG1_GLEventListener(CameraControl cameraControl, MyClock clock) {
    this.cameraControl = cameraControl;
    this.clock = clock;
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
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    clock.start();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
   // cameras.setAllPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    // messy having to set all cameras - cannot easily add a new camera whilst program is running
    // perhaps retrieve all cameras here instead
    // or store width,height as globals or aspect, then use these to handle setting camera perspective matrices
    // in another method.
    // perhaps a separate camera control class?

    Mat4 p = Mat4Transform.perspective(45, aspect);

    cameraControl.updateCameras(p);
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    // ignore
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;  // stores all the texture assets
  private Model cubeModel;  // separate class used for a cube model purely for illustration purposes
  private Light light;      // only one light
  private Robot robot;      // separate class used for a Robot which uses a scene graph
  private TransitionCollection transitions;  // stores all the transitions used in this class
  // private RandomNumberList randoms = new RandomNumberList(100);  // not used

  public void initialise(GL3 gl) {

    textures = loadTextures(gl);

    transitions = new TransitionCollection();

    light = makeLight(gl);
    //give Light a name so can find its transition?
    DoubleTransition transitionLight = makeLightTransition();
    transitions.add("light", transitionLight);
    
    cubeModel = new MyCubeModel(gl, "cube", light, textures, cameraControl.getCameraLibrary());

    robot = new Robot(gl, cameraControl.getCameraLibrary(), light, textures); 
    
    // use same name as makeCube so that the transition can be connected to the object
    //transitions.add(cubeModel.getName(), transitionCube);
    // could create a cubemodel class that extends Model and contains the transition information
    // that class could have as many transitions as needed, and could be used to change any aspect of the model 
    // including the shader, material, mesh, modelMatrix, light, camera etc.
  }
 
  public void render(GL3 gl) {
    updateAll();
    light.render(gl);
    cubeModel.render(gl);
    robot.render(gl);
    // why isn't light like any other object? 
    // just make it an object and then retrieve its position for rendering other objects?
    // draw it like any other object - could then add it to the scenegraph, although would
    // need to do some work to get its local position.
    // would make spotlight easier to get position.
  }

  private TextureLibrary loadTextures(GL3 gl) {
    TextureLibrary textures = new TextureLibrary();
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "jade_diffuse", "assets/textures/jade.jpg");
    textures.add(gl, "jade_specular", "assets/textures/jade_specular.jpg");
    textures.add(gl, "container_diffuse", "assets/textures/container2.jpg");
    textures.add(gl, "container_specular", "assets/textures/container2_specular.jpg");
    textures.add(gl, "watt_diffuse", "assets/textures/wattBook.jpg");
    textures.add(gl, "watt_specular", "assets/textures/wattBook_specular.jpg");
    textures.add(gl, "cloud", "assets/textures/cloud.jpg");
    textures.add(gl, "emission", "assets/textures/black1x1.jpg");
    return textures;
  }

  private Light makeLight(GL3 gl) {
    Light light = new Light(gl);
    light.setCameraLibrary(cameraControl.getCameraLibrary());
    return light;
  }

  private DoubleTransition makeLightTransition() {
    DoubleTransition transitionLight = new DoubleTransition(0.0,
      new double[]{0.5, 4.5, 5.5, 8.5}, 
      Arrays.asList(0.0,360.0,270.0,360.0), 
      true);
    //System.out.println(transitionLight);
    return transitionLight;
  }

  // one updateAll method or separate calls for relevant objects?
  // if model list call update on all models in the list, even if not needed?
  public void updateAll() {
    //System.out.println("update");
    double t = clock.getTime();
    double degrees = (Double)transitions.get("light").get(t);  // cast required as get returns Object
    double x = 8.0*Math.sin(Math.toRadians(degrees));
    double y = 0.4;
    double z = 8.0*Math.cos(Math.toRadians(degrees));
    light.setPosition((float)x,(float)y,(float)z);

    //((MyCubeModel)cubeModel).update(t);  // if update method in Model class then use polymorphism?
    cubeModel.update(t); 
    // use getName to get the name of the object and then use that to get the transition
    robot.update(t);
  }

}