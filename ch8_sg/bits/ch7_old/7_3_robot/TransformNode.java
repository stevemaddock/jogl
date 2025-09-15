import gmaths.*;
import com.jogamp.opengl.*;

public class TransformNode extends SGNode {

  private Mat4 transform;

  public TransformNode(String name, Mat4 t) {
    super(name);
    transform = new Mat4(t);
  }
  
  public void setTransform(Mat4 m) {
    transform = new Mat4(m);
  }
  
  protected void update(Mat4 t) {
    worldTransform = t;
    t = Mat4.multiply(worldTransform, transform);
    for (int i=0; i<children.size(); i++) {
      children.get(i).update(t);
    }   
  }

  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent)+"Name: "+name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
      System.out.println("transform node:");
      System.out.println(transform);
    }
    for (int i=0; i<children.size(); i++) {
      children.get(i).print(indent+1, inFull);
    }
  }
  
}