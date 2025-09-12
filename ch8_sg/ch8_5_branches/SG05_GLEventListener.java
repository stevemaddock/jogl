import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class SG05_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public SG05_GLEventListener(Camera camera) {
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
  
  private SGTransformNode translateX, rotateAll, rotateUpper;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;

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
    SGTransformNode translateToTop = new SGTransformNode("translate(0,"+lowerBranchHeight+",0)",
                                                     Mat4Transform.translate(0,lowerBranchHeight,0));
    // The next are global variables so they can be updated in other methods
    translateX = new SGTransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));  
    rotateAll = new SGTransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper = new SGTransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    
    twoBranchRoot.addChild(translateX);
      translateX.addChild(rotateAll);
        rotateAll.addChild(lowerBranch);
          lowerBranch.addChild(translateToTop);
            translateToTop.addChild(rotateUpper);
              rotateUpper.addChild(upperBranch);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // end of 5. 

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
    rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }

 // the following two methods are quite similar and could be replaced with one method with suitable parameterisation

  private SGNode makeLowerBranch(Model sphere, float height) {
    SGNameNode lowerBranchName = new SGNameNode("lower branch");
    Mat4 m = Mat4Transform.scale(2.5f,height,2.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode lowerBranch = new SGTransformNode("scale(2.5,4,2.5); translate(0,0.5,0)", m);
    SGModelNode sphereNode = new SGModelNode("Sphere(0)", sphere);
    lowerBranchName.addChild(lowerBranch);
      lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeUpperBranch(Model sphere) {
    SGNameNode upperBranchName = new SGNameNode("upper branch");
    Mat4 m = Mat4Transform.scale(1.4f,3.1f,1.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode upperBranch = new SGTransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
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