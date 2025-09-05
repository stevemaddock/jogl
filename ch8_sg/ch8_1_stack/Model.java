import gmaths.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class Model {
  
  protected String name;
  protected Mesh mesh;
  protected Mat4 modelMatrix;
  protected Shader shader;
  protected Material material;
  protected Camera camera;
  protected Light light;
  protected Renderer renderer;

  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    shader = null;
    renderer = null;
    light = null;
    camera = null;
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, 
    Material material, Renderer renderer,
    Light light, Camera camera) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.renderer = renderer;
    this.light = light;
    this.camera = camera;
  }

  public void setName(String s) {
    this.name = s;
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

  public void render(GL3 gl) {
    renderer.render(gl, mesh, modelMatrix, shader, material, light, camera);
  }

  // second version of render is so that modelMatrix can be overridden with a new parameter

  // This method assumes that the shader contains the variable names that are used in all the set methods.
  
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }
    renderer.render(gl, mesh, modelMatrix, shader, material, light, camera);
  } 
  
  private boolean mesh_null() {
    return (mesh==null);
  }
  
}