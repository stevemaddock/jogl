import gmaths.*;
import mycommon.*;
import mesh.*;
import animation.*;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.*;

public class MyCubeModel extends Model {  
  // does it need to extend Model? do I want to keep it in a list of models? 
  // will need instanceof when retrieving from modellist
  // or add an update method to Model class? which is overridden in this class
  // beware of getting into territory of making Model abstract?
  
  private Model cubeModel;
  private Mat4Transition transitionCube;

  public MyCubeModel(GL3 gl, String name, 
                     Light light, TextureLibrary textures, CameraLibrary cameras) {  
    //super();                 
    cubeModel = makeCube(gl, name, light, textures, cameras);
    transitionCube = makeCubeTransition();
  }

  private Model makeCube(GL3 gl, String name, Light light, TextureLibrary textures, CameraLibrary cameras) {
    // use same name for transition so can be connected to each other
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = Mat4Transform.translate(0,0,0);
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", 
                                 "assets/shaders/fs_standard_3t.txt");
    Material material = new Material(); //MaterialConstants.gold; //new Material(ambient,diffuse, specular, shininess);
    material.setDiffuseMap(textures.get("container_diffuse"));
    material.setSpecularMap(textures.get("container_specular"));
    material.setEmissionMap(textures.get("emission"));
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, cameras);
  }

  private Mat4Transition makeCubeTransition() {
    // initial size is 4,4,4 for the cube when it is made, so start at 4
    // alternative is to use the value in the transition as a multiplier
    double[] times2 = {1, 2.0, 5.5, 8.5};
    List<Mat4> cubeValues = Arrays.asList(
      Mat4Transform.translate(0,0,0), 
      Mat4Transform.translate(6,0,4), 
      Mat4Transform.translate(-6,0,4), 
      Mat4Transform.translate(0,0,0));
    Mat4Transition transitionCube = new Mat4Transition(0.0, times2, cubeValues, false);
    //System.out.println(transitionCube);
    return transitionCube;
  }

  public void update(double t) {
    Mat4 cubeMatrix = transitionCube.get(t);  // cast required as get returns Object
    cubeModel.setModelMatrix(cubeMatrix);
  }


  public void render(GL3 gl) {
    cubeModel.render(gl);
  }



}