import gmaths.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

// OVERLY COMPLICATED???
// Just use a single method with if tests for each available field?

public class Renderer {

  public Renderer() {}

  private void doVertexShaderMatrices(GL3 gl, Shader shader, Mat4 modelMatrix, Camera camera) { 
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), 
                                   Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    shader.setVec3(gl, "viewPos", camera.getPosition());
  }

  private void doSingleLight(GL3 gl, Shader shader, Light light) {
    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
  }

  private void doBasicMaterial(GL3 gl, Shader shader, Material material) {
    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());
  }


  // rename shader textures as diffuse_texture, specular_texture and emission_texture
  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1 and GL_TEXTURE2

  private void doDiffuseMap(GL3 gl, Shader shader, Texture dm) {
    shader.setInt(gl, "diffuse_texture", 0);  
    gl.glActiveTexture(GL.GL_TEXTURE0);
    dm.bind(gl);
  }

  private void doSpecularMap(GL3 gl, Shader shader, Texture sm) {
    shader.setInt(gl, "specular_texture", 1);  
    gl.glActiveTexture(GL.GL_TEXTURE1);
    sm.bind(gl);
  }

  private void doEmissionMap(GL3 gl, Shader shader, Texture em) {
    shader.setInt(gl, "emission_texture", 2);  
    gl.glActiveTexture(GL.GL_TEXTURE2);
    em.bind(gl);
  }

  public void render(GL3 gl, Mesh mesh, Mat4 modelMatrix,  Shader shader, 
      Material material, Light light, Camera camera) {
    // set shader variables. Be careful that these variables exist in the shader
    shader.use(gl);
    doVertexShaderMatrices(gl, shader, modelMatrix, camera);
    doSingleLight(gl, shader, light);
    doBasicMaterial(gl, shader, material);
    if (material.diffuseMapExists()) {
      Texture dm = material.getDiffuseMap();
      doDiffuseMap(gl, shader, dm);
    }
    if (material.specularMapExists()) {
      Texture sm = material.getSpecularMap();
      doSpecularMap(gl, shader, sm);
    }
    if (material.emissionMapExists()) {
      Texture em = material.getEmissionMap();
      doEmissionMap(gl, shader, em);
    }
    
    // then render the mesh
    mesh.render(gl);
  }

  public void renderPBR(GL3 gl, Mesh mesh, Mat4 modelMatrix,  Shader shader, 
                        Material material, Light light, Camera camera) {

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));

    // set shader variables. Be careful that these variables exist in the shader

    shader.use(gl);

    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
    shader.setVec3(gl, "light.colour", new Vec3(150,150,150));

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    // Assumes the textures exist - should be updated to check they exist
    // However, wouldn't be calling this method if they didn't,
    // since this is a test program
    
    Texture talbedo = material.getAlbedoMap();
    shader.setInt(gl, "albedoMap", 0);  
    gl.glActiveTexture(GL.GL_TEXTURE0);
    talbedo.bind(gl);

    Texture tnormal = material.getNormalMap();
    shader.setInt(gl, "normalMap", 1);  
    gl.glActiveTexture(GL.GL_TEXTURE1);
    tnormal.bind(gl);

    Texture tmetallic = material.getMetallicMap();
    shader.setInt(gl, "metallicMap", 2);  
    gl.glActiveTexture(GL.GL_TEXTURE2);
    tmetallic.bind(gl);

    Texture troughness = material.getRoughnessMap();
    shader.setInt(gl, "roughnessMap", 3);  
    gl.glActiveTexture(GL.GL_TEXTURE3);
    troughness.bind(gl);

    Texture tao = material.getAOMap();
    shader.setInt(gl, "aoMap", 4);  
    gl.glActiveTexture(GL.GL_TEXTURE4);
    tao.bind(gl);

    // then render the mesh
    mesh.render(gl);
  } 

}