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
  
public class M03_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M03_GLEventListener(Camera camera) {
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
     translateX.update(); // IMPORTANT - the scene graph has changed
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
  
  private TransformNode translateX, rotateAll, rotateUpper;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  
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
  
    float lowerBranchHeight = 4f;
    SGNode lowerBranch = makeLowerBranch(sphere, lowerBranchHeight);
    SGNode upperBranch = makeUpperBranch(sphere);
    
    // 1. draw just the lowerBranch
    // twoBranchRoot.addChild(lowerBranch);
    // twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes

    // 2. draw just the upperBranch
    // twoBranchRoot.addChild(upperBranch);
    // twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes

    // 3. draw both branches without any extra transforms to join them
    // result will be only one can be seen as they are both drawn at the origin,
    // so one is inside the other.
    // twoBranchRoot.addChild(lowerBranch);
    //   lowerBranch.addChild(upperBranch);
    // twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes

    // 4. Now join them together with a transform
    // TransformNode translateToTop = new TransformNode("translate(0,4,0)",Mat4Transform.translate(0,4,0));
    // twoBranchRoot.addChild(lowerBranch);
    //   lowerBranch.addChild(translateToTop);
    //     translateToTop.addChild(upperBranch);
    // twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes

    // 5. Now for some animation
    TransformNode translateToTop = new TransformNode("translate(0,"+lowerBranchHeight+",0)",
                                                     Mat4Transform.translate(0,lowerBranchHeight,0));
    // The next are global variables so they can be updated in other methods
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));  
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    
    twoBranchRoot.addChild(translateX);
      translateX.addChild(rotateAll);
        rotateAll.addChild(lowerBranch);
          lowerBranch.addChild(translateToTop);
            translateToTop.addChild(rotateUpper);
              rotateUpper.addChild(upperBranch);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // end of 5.
    
  }
 
  // the following two methods are quite similar and could be replaced with one method with suitable parameterisation

  private SGNode makeLowerBranch(Model sphere, float height) {
    NameNode lowerBranchName = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(2.5f,height,2.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lowerBranch = new TransformNode("scale(2.5,4,2.5); translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
    lowerBranchName.addChild(lowerBranch);
      lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeUpperBranch(Model sphere) {
    NameNode upperBranchName = new NameNode("upper branch");
    Mat4 m = Mat4Transform.scale(1.4f,3.1f,1.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode upperBranch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
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

    // 5. animation. Comment out the following line for 1-4.
    updateBranches();

    twoBranchRoot.draw(gl);
  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;
    rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
    rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }
  
  // The light's position is continually being changed, so needs to be calculated for each frame.
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
    Model sphere = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1, t2);
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