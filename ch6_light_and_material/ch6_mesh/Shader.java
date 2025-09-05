import gmaths.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.glsl.*;  
  
public class Shader {
  
  private static final boolean DISPLAY_SHADERS = false;
  
  private int ID;
  private String vertexShaderSource;
  private String fragmentShaderSource;
  
  /* The constructor */
  public Shader(GL3 gl, String vertexPath, String fragmentPath) {
    try {
      vertexShaderSource = new String(Files.readAllBytes(Paths.get(vertexPath)), Charset.defaultCharset());
      fragmentShaderSource = new String(Files.readAllBytes(Paths.get(fragmentPath)), Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (DISPLAY_SHADERS) display();
    ID = compileAndLink(gl);
  }
  
  public int getID() {
    return ID;
  }
  
  public void use(GL3 gl) {
    gl.glUseProgram(ID);
  }
  
  public void setInt(GL3 gl, String name, int value) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform1i(location, value);
  }
  
  public void setFloat(GL3 gl, String name, float value) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform1f(location, value);
  }
  
  public void setFloat(GL3 gl, String name, float f1, float f2) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform2f(location, f1, f2);
  }
  
  public void setFloat(GL3 gl, String name, float f1, float f2, float f3) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform3f(location, f1, f2, f3);
  }
  
  public void setFloat(GL3 gl, String name, float f1, float f2, float f3, float f4) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform4f(location, f1, f2, f3, f4);
  }
  
  public void setFloatArray(GL3 gl, String name, float[] f) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniformMatrix4fv(location, 1, false, f, 0);
  }
  
  public void setVec3(GL3 gl, String name, Vec3 v) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform3f(location, v.x, v.y, v.z);
  }
  
  private void display() {
    System.out.println("***Vertex shader***");
    System.out.println(vertexShaderSource);
    System.out.println("\n***Fragment shader***");
    System.out.println(fragmentShaderSource);
  }
  
  private int compileAndLink(GL3 gl) {
    String[][] sources = new String[1][1];
    sources[0] = new String[]{ vertexShaderSource };
    ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
    boolean compiled = vertexShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile vertex shader: " + sources);
    sources[0] = new String[]{ fragmentShaderSource };
    ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
    compiled = fragmentShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile fragment shader: " + sources);
    ShaderProgram program = new ShaderProgram();
    program.init(gl);
    program.add(vertexShaderCode);
    program.add(fragmentShaderCode);
    program.link(gl, System.out);
    if (!program.validateProgram(gl, System.out))
      System.err.println("[error] Unable to link program");
    return program.program();
  }

}