import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class SG03_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public SG03_GLEventListener(Camera camera) {
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

  private Model floor, cubeLower, cubeUpper, sphere, fStemCube, fTopCube, fMiddleCube;
  private Light light;
  private SGNode cuboidsRoot, fShapeRoot;

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

    floor = makeFloor(gl, getM1(), "assets/shaders/fs_standard_d.txt", textures.get("chequerboard"), null, null);
   
    Material materialCube = MaterialConstants.green.clone();
    cubeLower = makeCube(gl, new Mat4(1), materialCube,
                       "assets/shaders/fs_standard_0t.txt", 
                       null,null,null);

    materialCube = MaterialConstants.red.clone();
    cubeUpper = makeCube(gl, new Mat4(1), materialCube,
                       "assets/shaders/fs_standard_0t.txt", 
                       null,null,null);

    materialCube = MaterialConstants.blue.clone();
    fStemCube = makeCube(gl, new Mat4(1), materialCube,
                       "assets/shaders/fs_standard_0t.txt", 
                       null,null,null);

    fTopCube = makeCube(gl, new Mat4(1), materialCube,
                       "assets/shaders/fs_standard_0t.txt", 
                       null,null,null);

    fMiddleCube = makeCube(gl, new Mat4(1), materialCube,
                       "assets/shaders/fs_standard_0t.txt", 
                       null,null,null);

    Mat4 mSphere = Mat4Transform.translate(0,0.5f,0);
    mSphere = Mat4.multiply(Mat4Transform.scale(2,4,2),mSphere);
    sphere = makeSphere(gl, mSphere,
                       "assets/shaders/fs_standard_d.txt", 
                       textures.get("cloud"), null, null);


    cuboidsRoot = new SGNameNode("two-cuboid structure");
    SGTransformNode translateInPlane = new SGTransformNode("translate(1.7,0,1.3)",Mat4Transform.translate(2.7f, 0f, 2.3f));
    SGNameNode lowerCuboid = new SGNameNode("lower cuboid");
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
        lowerCuboid.addChild(lowerCubeTransform);
          lowerCubeTransform.addChild(lowerCubeShape);
        lowerCuboid.addChild(translateToTop);
          translateToTop.addChild(upperCuboid);
            upperCuboid.addChild(upperCubeTransform);
              upperCubeTransform.addChild(upperCubeShape);
      cuboidsRoot.update(); 

    fShapeRoot = new SGNameNode("f structure");
    SGTransformNode translateFInPlane = new SGTransformNode("translate(0.2,0,1.7)",Mat4Transform.translate(0.2f,0f,2.7f));

    SGNameNode fStem = new SGNameNode("fStem");
    m = Mat4Transform.scale(0.5f,3,1);
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
      translateFInPlane.addChild(fStem);
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
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame
    light.render(gl);
    floor.render(gl);
    sphere.render(gl);
    
    cuboidsRoot.draw(gl);
    fShapeRoot.draw(gl);
  }
  

  // **********************************
  /* Floor
   */

  private Model makeFloor(GL3 gl, Mat4 m, String fragmentPath, Texture diffuse, Texture specular, Texture emission) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mat4 modelMatrix = m;
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

  private Mat4 getModelMatrix(int i) {
    double elapsedTime = getSeconds()-startTime;
    Mat4 model = new Mat4(1);    
    float yAngle = (float)(elapsedTime*10*randoms[(i+637)%NUM_RANDOMS]);
    float multiplier = 12.0f;
    float x = multiplier*randoms[i%NUM_RANDOMS] - multiplier*0.5f;
    float y = 0.5f+ (multiplier*0.5f) + multiplier*randoms[(i+137)%NUM_RANDOMS] - multiplier*0.5f;
    float z = multiplier*randoms[(i+563)%NUM_RANDOMS] - multiplier*0.5f;
    model = Mat4.multiply(model, Mat4Transform.translate(x,y,z));
    model = Mat4.multiply(model, Mat4Transform.rotateAroundY(yAngle));
    return model;
  }

  private Mat4 getCubeModelMatrix() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    return modelMatrix;
  }

  private Model makeCube(GL3 gl, Mat4 m, Material materialIn, String fragmentPath, Texture diffuse, Texture specular, Texture emission) {
    String name = "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = m;
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", fragmentPath);
    Material material = materialIn.clone();
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

 
  private Mat4 getM1() {
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    return modelMatrix;
  }
  
  private Mat4 getM2() {
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    return modelMatrix;
  }

  private Mat4 getM3() {
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-size*0.5f,size*0.5f,0), modelMatrix);
    return modelMatrix;
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