import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class SG04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public SG04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,6f,15f));
    this.camera.setTarget(new Vec3(0f,0,0f));
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

  private Model sphere;
  private Floor floor;
  private FObject fObj;
  private Cuboids cuboids;

  private Light light;

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

    light = new Light(gl, camera);
    Material material = new Material();
    material.setAmbient(0.3f, 0.3f, 0.3f);
    material.setDiffuse(0.7f, 0.7f, 0.7f);
    material.setSpecular(0.7f, 0.7f, 0.7f);
    light.setMaterial(material);

    floor = new Floor(gl, light, camera, textures.get("chequerboard"));
    fObj = new FObject(gl, light, camera);
    cuboids = new Cuboids(gl, light, camera);

    Mat4 mSphere = Mat4Transform.translate(0,0.5f,0);
    mSphere = Mat4.multiply(Mat4Transform.scale(2,4,2),mSphere);
    sphere = makeSphere(gl, mSphere,
                       "assets/shaders/fs_standard_d.txt", 
                       textures.get("cloud"), null, null);
  }


  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame
    light.render(gl);

    floor.render(gl);
    sphere.render(gl);

    cuboids.render(gl);
    fObj.render(gl);
  }
  

  // **********************************

  /* interaction menu */

  public void setFAngle(float angle) {
    fObj.setAngle(angle);
  }

  public void setCAngle(float angle) {
    cuboids.setAngle(angle);
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
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    return modelMatrix;
  }
}

class FObject {

  private Model fStemCube, fTopCube, fMiddleCube;
  private SGNode fShapeRoot;
  private float angle = 0;
  SGTransformNode rotateAroundY;

  public FObject(GL3 gl, Light light, Camera camera) {
    String fragmentPath = "assets/shaders/fs_standard_0t.txt";
    Material materialCube = MaterialConstants.blue.clone();
    fStemCube = makeCube(gl, light, camera);
    fTopCube = makeCube(gl, light, camera);
    fMiddleCube = makeCube(gl, light, camera);
    makeSG();
  }

  public void setAngle(float angle) {
    this.angle = angle;
    rotateAroundY.setTransform(Mat4Transform.rotateAroundY(angle));
    fShapeRoot.update();
  }

  private Model makeCube(GL3 gl, Light light, Camera camera) {
    String name = "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = new Mat4(1);
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_0t.txt");
    Material material = MaterialConstants.blue.clone();
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  private void makeSG() {
    fShapeRoot = new SGNameNode("f structure");
    SGTransformNode translateFInPlane = new SGTransformNode("translate(0.2,0,1.7)", Mat4Transform.translate(0.2f,0f,2.7f));

    rotateAroundY = new SGTransformNode("rotate("+angle+",y)", Mat4Transform.rotateAroundY(angle));

    SGNameNode fStem = new SGNameNode("fStem");
    Mat4 m = Mat4Transform.scale(0.5f,3,1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode fStemTransform = new SGTransformNode("some transforms", m);
    SGModelNode fStemShape = new SGModelNode("cubeLower", fStemCube);

    SGNameNode fMiddle = new SGNameNode("fMiddle");
    m = Mat4Transform.translate(0.5f,1f,0);
    m = Mat4.multiply(m, Mat4Transform.scale(1,0.5f,1));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode fMiddleTransform = new SGTransformNode("some transforms", m);
    SGModelNode fMiddleShape = new SGModelNode("cubeLower", fMiddleCube);

    SGNameNode fTop = new SGNameNode("fTop");
    m = Mat4Transform.translate(0.5f,2.5f,0);
    m = Mat4.multiply(m, Mat4Transform.scale(1.5f,0.5f,1));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode fTopTransform = new SGTransformNode("some transforms", m);
    SGModelNode fTopShape = new SGModelNode("cubeLower", fTopCube);

    fShapeRoot.addChild(translateFInPlane);
      translateFInPlane.addChild(rotateAroundY);  // added
      rotateAroundY.addChild(fStem);
        fStem.addChild(fStemTransform);
          fStemTransform.addChild(fStemShape);
        fStem.addChild(fMiddle);
          fMiddle.addChild(fMiddleTransform);
            fMiddleTransform.addChild(fMiddleShape);
        fStem.addChild(fTop);
          fTop.addChild(fTopTransform);
            fTopTransform.addChild(fTopShape);
    fShapeRoot.update();
  }

  public void render(GL3 gl) {
    fShapeRoot.draw(gl);
  }

}

class Cuboids {

  private Model cubeLower, cubeUpper;
  private SGNode cuboidsRoot;
  private float angle;
  private SGTransformNode rotateAroundY;

  public Cuboids(GL3 gl, Light light, Camera camera) {
    String fragmentPath = "assets/shaders/fs_standard_0t.txt";
    Material materialCube = MaterialConstants.blue.clone();
    cubeLower = makeCube(gl, light, camera, MaterialConstants.green.clone());
    cubeUpper = makeCube(gl, light, camera, MaterialConstants.red.clone());
    makeSG();
  }

  public void setAngle(float angle) {
    this.angle = angle;
    rotateAroundY.setTransform(Mat4Transform.rotateAroundY(angle));
    cuboidsRoot.update();
  }

  private Model makeCube(GL3 gl, Light light, Camera camera, Material material) {
    String name = "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = new Mat4(1);
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_0t.txt");
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  private void makeSG() {
    cuboidsRoot = new SGNameNode("two-cuboid structure");

    SGTransformNode translateInPlane = new SGTransformNode("translate(1.7,0,1.3)",Mat4Transform.translate(2.7f, 0f, 2.3f));

    SGNameNode lowerCuboid = new SGNameNode("lower cuboid");
    rotateAroundY = new SGTransformNode("rotate("+angle+",y)", Mat4Transform.rotateAroundY(angle));
    Mat4 m = Mat4Transform.scale(1,1,1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode lowerCubeTransform = new SGTransformNode("some transforms", m);
    SGModelNode lowerCubeShape = new SGModelNode("cubeLower", cubeLower);

    SGTransformNode translateToTop = new SGTransformNode("translate(0,4,0)",Mat4Transform.translate(0,0,0));

    SGNameNode upperCuboid = new SGNameNode("upper cuboid");
    m = Mat4Transform.scale(0.6f,3.5f,0.6f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode upperCubeTransform = new SGTransformNode("some transforms", m);
    SGModelNode upperCubeShape = new SGModelNode("cubeUpper", cubeUpper);

    cuboidsRoot.addChild(translateInPlane);
      translateInPlane.addChild(lowerCuboid);
        lowerCuboid.addChild(rotateAroundY);
          rotateAroundY.addChild(lowerCubeTransform);  // added
          lowerCubeTransform.addChild(lowerCubeShape);
        lowerCuboid.addChild(translateToTop);
          translateToTop.addChild(upperCuboid);
            upperCuboid.addChild(upperCubeTransform);
              upperCubeTransform.addChild(upperCubeShape);
      cuboidsRoot.update(); 
  }

  public void render(GL3 gl) {
    cuboidsRoot.draw(gl);
  }
}