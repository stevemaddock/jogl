import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
  
public class M03two_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M03two_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
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
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    textures.destroy(gl);
  }

    
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
   public void incXPosition() {
     xPosition += 0.5f;
     if (xPosition>5f) xPosition = 5f;
     updateX();
   }
   
   public void decXPosition() {
     xPosition -= 0.5f;
     if (xPosition<-5f) xPosition = -5f;
     updateX();
   }
   
   private void updateX() {
     translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
     translateX.update(); // IMPORTANT – the scene graph has changed
   }
   
   
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere;
  private Light light;
  private SGNode twoBranchRoot;
  
  private TransformNode translateX, rotateAll, rotateUpper1, rotateUpper2;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpper1AngleStart = -60, rotateUpper1Angle = rotateUpper1AngleStart;
  private float rotateUpper2AngleStart = 30, rotateUpper2Angle = rotateUpper2AngleStart;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "diffuse", "assets/textures/jade.jpg");
    textures.add(gl, "specular", "assets/textures/jade_specular.jpg");
    
    light = new Light(gl);
    light.setCamera(camera);

    floor = makeFloor(gl, textures.get("chequerboard"));
    sphere = makeSphere(gl, textures.get("diffuse"), textures.get("specular"));
    
    twoBranchRoot = new NameNode("two-branch structure");
  
    float lowerBranchHeight = 4.0f;

    SGNode lowerBranch = makeLowerBranch(sphere, 2.5f,lowerBranchHeight,2.5f);
    SGNode upperBranch1 = makeUpperBranch(sphere, 1.4f,3.1f,1.4f);
    SGNode upperBranch2 = makeUpperBranch(sphere, 0.6f,1.4f,0.6f);

    TransformNode translateToTop1 = new TransformNode("translate(0,"+lowerBranchHeight+",0)",Mat4Transform.translate(0,lowerBranchHeight,0));
    TransformNode translateToTop2 = new TransformNode("translate(0,"+lowerBranchHeight+",0)",Mat4Transform.translate(0,lowerBranchHeight,0));
    // The next few are global variables so they can be updated in other methods
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));  
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper1 = new TransformNode("rotateAroundZ("+rotateUpper1Angle+")",Mat4Transform.rotateAroundZ(rotateUpper1Angle));
    rotateUpper2 = new TransformNode("rotateAroundZ("+rotateUpper2Angle+")",Mat4Transform.rotateAroundZ(rotateUpper2Angle));

    twoBranchRoot.addChild(translateX);
      translateX.addChild(rotateAll);
        rotateAll.addChild(lowerBranch);
          lowerBranch.addChild(translateToTop1);
            translateToTop1.addChild(rotateUpper1);
              rotateUpper1.addChild(upperBranch1);
          lowerBranch.addChild(translateToTop2);     // translateToTop1 could be used here as this is not an animated value
            translateToTop2.addChild(rotateUpper2);  // and here
              rotateUpper2.addChild(upperBranch2);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    
  }
 
  // the following two methods are quite similar and could be replaced with one method with suitable parameterisation
  private SGNode makeLowerBranch(Model sphere, float sx, float sy, float sz) {
    NameNode lowerBranchName = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(sx,sy,sx);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lowerBranch = new TransformNode("scale("+sx+","+sy+","+sz+"); translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
    lowerBranchName.addChild(lowerBranch);
      lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeUpperBranch(Model sphere, float sx, float sy, float sz) {
    NameNode upperBranchName = new NameNode("upper branch");
    Mat4 m = Mat4Transform.scale(sx,sy,sz);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode upperBranch = new TransformNode("scale("+sx+","+sy+","+sz+");translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
    upperBranchName.addChild(upperBranch);
      upperBranch.addChild(sphereNode);
    return upperBranchName;
  }


  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    updateBranches();
    twoBranchRoot.draw(gl);
  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;
    rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
    rotateUpper1Angle = rotateUpper1AngleStart*(float)Math.sin(elapsedTime*0.7f);
    rotateUpper2Angle = rotateUpper2AngleStart*(float)Math.sin(elapsedTime*0.7f);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper1.setTransform(Mat4Transform.rotateAroundZ(rotateUpper1Angle));
    rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(rotateUpper2Angle));
    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  private Model makeFloor(GL3 gl, Texture t1) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    Model floor = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1);
    return floor;
  }

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return sphere;
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