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

 /**
 * This class stores the Floor
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Floor {

  private Camera camera;
  private Light light;

  private Model floor;
   
  public Floor(GL3 gl, float xSize, float zSize, Camera cameraIn, Light lightIn, Texture texture1) {

    camera = cameraIn;
    light = lightIn;

    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(xSize,1f,zSize);
    floor = new Model(name, mesh, modelMatrix, shader, material, light, camera, texture1);

  }

  public void render(GL3 gl) {
    floor.render(gl);
  }

}