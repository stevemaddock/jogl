import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
  
public class L01_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Camera camera;
    
  /* The constructor is not used to initialise anything */
  public L01_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(-10,12,20));
    this.camera.setTarget(new Vec3(0,5,0));
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
    // GL3 gl = drawable.getGL().getGL3();
    // gl.glDeleteBuffers(1, vertexBufferId, 0);
    // gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    // gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  // interaction

  public void setSmile(boolean smile) {
    this.smile = smile;
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  public boolean smile = true;

  private Mesh cube;
  public Mesh sphere;
  private Mesh light;

  private Vec3 cubeAmbient = new Vec3(1.0f, 0.5f, 0.31f);
  private Vec3 cubeDiffuse = new Vec3(1.0f, 0.5f, 0.31f);
  private Vec3 cubeSpecular = new Vec3(0.5f, 0.5f, 0.5f);
  private float cubeShininess = 32.0f;

  private Shader cubeShader, sphereShader, lightShader;

  // textures
  private TextureLibrary textures;
  private Texture cubeDiffuseMap, sphereSmileDiffuseMap, sphereFrownDiffuseMap;
  private Texture cubeSpecularMap;
  private Texture cubeEmissionMap;

  private Vec3 lightPosition = new Vec3(4f,5f,8f);
  private Vec3 lightAmbient = new Vec3(0.2f, 0.2f, 0.2f);
  private Vec3 lightDiffuse = new Vec3(0.9f, 0.9f, 0.9f);
  private Vec3 lightSpecular = new Vec3(0.9f, 0.9f, 0.9f);

  public void initialise(GL3 gl) { 

    textures = new TextureLibrary();
    textures.add(gl, "diffuse_container", "assets/textures/container2.jpg");
    textures.add(gl, "specular_container", "assets/textures/container2_specular.jpg");
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "cloud", "assets/textures/cloud.jpg");
    textures.add(gl, "emission", "assets/textures/matrix.jpg");
    textures.add(gl, "smile", "assets/textures/smile.png");
    textures.add(gl, "frown", "assets/textures/frown.png");

    cube = new Mesh(gl, Cube.vertices, Cube.indices);
    sphere = new Mesh(gl, Sphere.vertices, Sphere.indices);
    light = new Mesh(gl, Sphere.vertices, Sphere.indices);

    cubeShader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_dse.txt");
    cubeDiffuseMap = textures.get("diffuse_container");
    cubeSpecularMap = textures.get("specular_container");
    cubeEmissionMap = textures.get("emission");

    sphereShader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_d.txt");
    sphereSmileDiffuseMap = textures.get("smile");
    sphereFrownDiffuseMap = textures.get("frown");

    lightShader = new Shader(gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
  }
  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    lightPosition = getLightPosition();  // changing light position each frame

    Mat4 projectionMatrix = camera.getPerspectiveMatrix();
    Mat4 viewMatrix = camera.getViewMatrix();

    renderLight(gl, lightShader, getLightModelMatrix(), viewMatrix, projectionMatrix);
    renderCube(gl, cubeShader, getCubeModelMatrix(), viewMatrix, projectionMatrix);
    renderSphere(gl, sphereShader, getSphereModelMatrix(), viewMatrix, projectionMatrix);
}
  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  // **********************************
  /* Rendering the cube
   */
   
  private Mat4 getCubeModelMatrix() {
    Mat4 modelMatrix = Mat4Transform.translate(0,0.5f,0);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4f,4f,4f), modelMatrix);
    return modelMatrix;
  }
  
  private void renderCube(GL3 gl, Shader shader, Mat4 modelMatrix, Mat4 viewMatrix, Mat4 projectionMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(projectionMatrix, Mat4.multiply(viewMatrix, modelMatrix));
    
    shader.use(gl);
  
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", lightPosition);
    shader.setVec3(gl, "light.ambient", lightAmbient);
    shader.setVec3(gl, "light.diffuse", lightDiffuse);
    shader.setVec3(gl, "light.specular", lightSpecular);

    shader.setVec3(gl, "material.ambient", cubeAmbient);
    shader.setVec3(gl, "material.diffuse", cubeDiffuse);
    shader.setVec3(gl, "material.specular", cubeSpecular);
    shader.setFloat(gl, "material.shininess", cubeShininess);

    if (cubeDiffuseMap!=null) {
      shader.setInt(gl, "diffuse_texture", 0);  
      gl.glActiveTexture(GL.GL_TEXTURE0);
      cubeDiffuseMap.bind(gl);
    }
    if (cubeSpecularMap!=null) {
      shader.setInt(gl, "specular_texture", 1);  
      gl.glActiveTexture(GL.GL_TEXTURE1);
      cubeSpecularMap.bind(gl);
    }
    if (cubeEmissionMap!=null) {
      shader.setInt(gl, "emission_texture", 2);  
      gl.glActiveTexture(GL.GL_TEXTURE2);
      cubeEmissionMap.bind(gl);
    }

    cube.render(gl);
  }

  // **********************************
  /* Rendering the sphere
   */
  private Mat4 getSphereModelMatrix() {
    Mat4 modelMatrix = Mat4Transform.translate(0f,0.5f,0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(5f,5f,5f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0f,4f,0f), modelMatrix);
    return modelMatrix;
  }

  // same light properties
  // use same material as cube, except for texture maps

  private void renderSphere(GL3 gl, Shader shader, Mat4 modelMatrix, Mat4 viewMatrix, Mat4 projectionMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(projectionMatrix, Mat4.multiply(viewMatrix, modelMatrix));
    
    shader.use(gl);
  
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", lightPosition);
    shader.setVec3(gl, "light.ambient", lightAmbient);
    shader.setVec3(gl, "light.diffuse", lightDiffuse);
    shader.setVec3(gl, "light.specular", lightSpecular);

    shader.setVec3(gl, "material.ambient", cubeAmbient);
    shader.setVec3(gl, "material.diffuse", cubeDiffuse);
    shader.setVec3(gl, "material.specular", cubeSpecular);
    shader.setFloat(gl, "material.shininess", cubeShininess);

    Texture sphereDiffuseMap = sphereSmileDiffuseMap;
    // if (!smile) {
    //   sphereDiffuseMap = sphereFrownDiffuseMap;
    // }

    // alternative
    if (lightPosition.z > 0) {
      sphereDiffuseMap = sphereSmileDiffuseMap;
    }
    else {
      sphereDiffuseMap = sphereFrownDiffuseMap;
    }

    if (sphereDiffuseMap!=null) {
      shader.setInt(gl, "diffuse_texture", 0);  
      gl.glActiveTexture(GL.GL_TEXTURE0);
      sphereDiffuseMap.bind(gl);
    }
    // no specular or emission map for sphere

    sphere.render(gl);
  }

  // **********************************
  /* Rendering the light as an object
   */


  private Mat4 getLightModelMatrix() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(lightPosition), modelMatrix);
    return modelMatrix;
  }

    // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 3.4f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  private void renderLight(GL3 gl, Shader shader, Mat4 modelMatrix, Mat4 view, Mat4 projection) {
    Mat4 mvpMatrix = Mat4.multiply(projection, Mat4.multiply(view, modelMatrix));
    
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    shader.setVec3(gl, "lightColor", lightDiffuse);

    light.render(gl);
  }

}