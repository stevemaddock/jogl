import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

import com.jogamp.opengl.util.texture.*;
  
public class PBR01_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shaderCube, shaderLight;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public PBR01_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(6f,6f,25f));
    this.camera.setTarget(new Vec3(0f,0f,0f));
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
  private Model[] sphere;
  private Light light;

  // textures
  private TextureLibrary textures;

  public void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "wall_albedo", "assets/textures/wall/albedo.png");
    textures.add(gl, "wall_ao", "assets/textures/wall/ao.png");
    textures.add(gl, "wall_metallic", "assets/textures/wall/metallic.png");
    textures.add(gl, "wall_normal", "assets/textures/wall/normal.png");
    textures.add(gl, "wall_roughness", "assets/textures/wall/roughness.png");

    textures.add(gl, "plastic_albedo", "assets/textures/plastic/albedo.png");
    textures.add(gl, "plastic_ao", "assets/textures/plastic/ao.png");
    textures.add(gl, "plastic_metallic", "assets/textures/plastic/metallic.png");
    textures.add(gl, "plastic_normal", "assets/textures/plastic/normal.png");
    textures.add(gl, "plastic_roughness", "assets/textures/plastic/roughness.png");

    textures.add(gl, "rusted_iron_albedo", "assets/textures/rusted_iron/albedo.png");
    textures.add(gl, "rusted_iron_ao", "assets/textures/rusted_iron/ao.png");
    textures.add(gl, "rusted_iron_metallic", "assets/textures/rusted_iron/metallic.png");
    textures.add(gl, "rusted_iron_normal", "assets/textures/rusted_iron/normal.png");
    textures.add(gl, "rusted_iron_roughness", "assets/textures/rusted_iron/roughness.png");

    textures.add(gl, "carpet1_albedo", "assets/textures/carpet1/albedo.png");
    textures.add(gl, "carpet1_ao", "assets/textures/carpet1/ao.png");
    textures.add(gl, "carpet1_metallic", "assets/textures/carpet1/metallic.png");
    textures.add(gl, "carpet1_normal", "assets/textures/carpet1/normal.png");
    textures.add(gl, "carpet1_roughness", "assets/textures/carpet1/roughness.png");

    textures.add(gl, "stonetile_albedo", "assets/textures/stonetile/albedo.png");
    textures.add(gl, "stonetile_ao", "assets/textures/stonetile/ao.png");
    textures.add(gl, "stonetile_metallic", "assets/textures/stonetile/metallic.png");
    textures.add(gl, "stonetile_normal", "assets/textures/stonetile/normal.png");
    textures.add(gl, "stonetile_roughness", "assets/textures/stonetile/roughness.png");

    textures.add(gl, "alien_panels_albedo", "assets/textures/alien_panels/albedo.png");
    textures.add(gl, "alien_panels_ao", "assets/textures/alien_panels/ao.png");
    textures.add(gl, "alien_panels_metallic", "assets/textures/alien_panels/metallic.png");
    textures.add(gl, "alien_panels_normal", "assets/textures/alien_panels/normal.png");
    textures.add(gl, "alien_panels_roughness", "assets/textures/alien_panels/roughness.png");

    light = new Light(gl, camera);
    Material m = new Material();
    m.setAmbient(0.3f, 0.3f, 0.3f);
    m.setDiffuse(0.7f, 0.7f, 0.7f);
    m.setSpecular(0.7f, 0.7f, 0.7f);
    light.setMaterial(m);

    cube = new Model[3];

    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-8,0,0), modelMatrix);
    cube[0] = makeCube(gl, modelMatrix,
                       textures.get("alien_panels_albedo"), textures.get("alien_panels_normal"), textures.get("alien_panels_metallic"),
                       textures.get("alien_panels_roughness"), textures.get("alien_panels_ao"));

    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,0,0), modelMatrix);
    cube[1] = makeCube(gl, modelMatrix,
                       textures.get("carpet1_albedo"), textures.get("carpet1_normal"), textures.get("carpet1_metallic"), 
                       textures.get("carpet1_roughness"), textures.get("carpet1_ao"));

    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(4,0,0), modelMatrix);
    cube[2] = makeCube(gl, modelMatrix,
                       textures.get("stonetile_albedo"), textures.get("stonetile_normal"), textures.get("stonetile_metallic"), 
                       textures.get("stonetile_roughness"), textures.get("stonetile_ao"));

    sphere = new Model[2];
    
    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-4,0,0), modelMatrix);
    sphere[0] = makeSphere(gl, modelMatrix,
                       textures.get("plastic_albedo"), textures.get("plastic_normal"), textures.get("plastic_metallic"), 
                       textures.get("plastic_roughness"), textures.get("plastic_ao"));

    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(8,0,0), modelMatrix);
    sphere[1] = makeSphere(gl, modelMatrix,
                       textures.get("rusted_iron_albedo"), textures.get("rusted_iron_normal"), textures.get("rusted_iron_metallic"), 
                       textures.get("rusted_iron_roughness"), textures.get("rusted_iron_ao"));

  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame

    light.render(gl);
    
    cube[0].renderPBR(gl);
    cube[1].renderPBR(gl);
    cube[2].renderPBR(gl);

    sphere[0].renderPBR(gl);
    sphere[1].renderPBR(gl);

  }

  // **********************************
  /* Cube
   */

  private Model makeCube(GL3 gl, Mat4 m, Texture albedo, Texture normal, Texture metallic, Texture roughness, Texture ao) {
    String name = "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = m;
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_pbr_5t.txt");
    Material material = new Material(); 
    material.setAlbedoMap(albedo);
    material.setNormalMap(normal);
    material.setMetallicMap(metallic);
    material.setRoughnessMap(roughness);
    material.setAOMap(ao);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  // **********************************
  /* Sphere
   */

  private Model makeSphere(GL3 gl, Mat4 m,  Texture albedo, Texture normal, Texture metallic, Texture roughness, Texture ao) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Mat4 modelMatrix = m;
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_pbr_5t.txt");
    Material material = new Material();
    material.setAlbedoMap(albedo);
    material.setNormalMap(normal);
    material.setMetallicMap(metallic);
    material.setRoughnessMap(roughness);
    material.setAOMap(ao);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
  }

  // **********************************
  /* Light
   */

    // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 12.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 1.4f;
    float z = 12.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
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