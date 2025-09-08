import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class SG06_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public SG06_GLEventListener(Camera camera) {
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

     // textures
  private TextureLibrary textures;

  private Model sphere;
  private Model floor;
  private Light light;
  private SGNode twoBranchRoot;
  
  private SGTransformNode translateX, rotateAll, rotateUpper1, rotateUpper2;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpper1AngleStart = -60, rotateUpper1Angle = rotateUpper1AngleStart;
  private float rotateUpper2AngleStart = 30, rotateUpper2Angle = rotateUpper2AngleStart;

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

    light = new Light(gl, camera);
    Material m = new Material();
    m.setAmbient(0.3f, 0.3f, 0.3f);
    m.setDiffuse(0.7f, 0.7f, 0.7f);
    m.setSpecular(0.7f, 0.7f, 0.7f);
    light.setMaterial(m);

    floor = makeFloor(gl, "assets/shaders/fs_standard_d.txt", textures.get("chequerboard"), null, null);
   
    sphere = makeSphere(gl, new Mat4(1),
                       "assets/shaders/fs_standard_d.txt", 
                       textures.get("cloud"), null, null);

    twoBranchRoot = new SGNameNode("two-branch structure");
  
    float lowerBranchHeight = 4f;
    SGNode lowerBranch = makeLowerBranch(sphere, 2.5f,lowerBranchHeight,2.5f);
    SGNode upperBranch1 = makeUpperBranch(sphere, 1.4f,3.1f,1.4f);
    SGNode upperBranch2 = makeUpperBranch(sphere, 0.6f,1.4f,0.6f);

    SGTransformNode translateToTop1 = new SGTransformNode("translate(0,"+lowerBranchHeight+",0)",Mat4Transform.translate(0,lowerBranchHeight,0));
    SGTransformNode translateToTop2 = new SGTransformNode("translate(0,"+lowerBranchHeight+",0)",Mat4Transform.translate(0,lowerBranchHeight,0));
    // The next few are global variables so they can be updated in other methods
    translateX = new SGTransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));  
    rotateAll = new SGTransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper1 = new SGTransformNode("rotateAroundZ("+rotateUpper1Angle+")",Mat4Transform.rotateAroundZ(rotateUpper1Angle));
    rotateUpper2 = new SGTransformNode("rotateAroundZ("+rotateUpper2Angle+")",Mat4Transform.rotateAroundZ(rotateUpper2Angle));

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

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame

    light.render(gl);

    floor.render(gl);
    
  // 5. animation. Comment out the following line for 1-4.
    updateBranches();

    twoBranchRoot.draw(gl);
  }

  // **********************************
  /* branches
  */

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

// the following two methods are quite similar and could be replaced with one method with suitable parameterisation
  private SGNode makeLowerBranch(Model sphere, float sx, float sy, float sz) {
    SGNameNode lowerBranchName = new SGNameNode("lower branch");
    Mat4 m = Mat4Transform.scale(sx,sy,sx);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode lowerBranch = new SGTransformNode("scale("+sx+","+sy+","+sz+"); translate(0,0.5,0)", m);
    SGModelNode sphereNode = new SGModelNode("Sphere(0)", sphere);
    lowerBranchName.addChild(lowerBranch);
      lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeUpperBranch(Model sphere, float sx, float sy, float sz) {
    SGNameNode upperBranchName = new SGNameNode("upper branch");
    Mat4 m = Mat4Transform.scale(sx,sy,sz);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode upperBranch = new SGTransformNode("scale("+sx+","+sy+","+sz+");translate(0,0.5,0)", m);
    SGModelNode sphereNode = new SGModelNode("Sphere(1)", sphere);
    upperBranchName.addChild(upperBranch);
      upperBranch.addChild(sphereNode);
    return upperBranchName;
  }

  // **********************************
  /* Floor
   */

  private Model makeFloor(GL3 gl, String fragmentPath, Texture diffuse, Texture specular, Texture emission) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", fragmentPath);
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(specular);
    material.setEmissionMap(emission);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
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
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  // **********************************
  /* Sphere
   */

  private Model makeSphere(GL3 gl, Mat4 m, String fragmentPath, Texture diffuse, Texture specular, Texture emission) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Mat4 modelMatrix = m;
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", fragmentPath);
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(specular);
    material.setEmissionMap(emission);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  // **********************************
  /* Light
   */

    // The light's postion is continually being changed, so needs to be calculated for each frame.
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