package mycommon;

import gmaths.*;
import mesh.*;

import com.jogamp.opengl.*;

public class Model { 

  private String name;
  private Mesh mesh;
  private Mat4 modelMatrix;
  private Shader shader;
  private Material material;
  private CameraLibrary cameras;
  private Light light;
  private Renderer renderer;

  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    shader = null;
    renderer = null;
    light = null;
    cameras = null;
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, 
    Material material, Renderer renderer,
    Light light, CameraLibrary cameras) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.renderer = renderer;
    this.light = light;
    this.cameras = cameras;
  }

  public void setName(String s) {
    this.name = s;
  }

  public String getName() {
    return this.name;
  }

  public void setMesh(Mesh m) {
    this.mesh = m;
  }

  public Mesh getMesh() {
    return mesh;
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }

  public Material getMaterial() {
    return material;
  }

  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public Shader getShader() {
    return shader;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public Light getLight() {
    return light;
  }

  public void displayName(GL3 gl) {
    System.out.println("Name = "+name);  
  }

  public void update(double t) {}  
  // to be overridden in subclasses but not used in this class
  // but don't make it abstract as it is not used in all subclasses 
  // could make it abstract and then have a mull implementation in this class

  public void render(GL3 gl) {
    renderer.render(gl, mesh, modelMatrix, shader, material, light, cameras.getCurrentCamera());
  }

  // second version of render is so that modelMatrix can be overridden with a new parameter

  // This method assumes that the shader contains the variable names that are used in all the set methods.
  
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }
    renderer.render(gl,  mesh, modelMatrix,  shader, material,  light,  cameras.getCurrentCamera());
  } 
  
  private boolean mesh_null() {
    return (mesh==null);
  }
  
}