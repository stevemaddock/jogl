import com.jogamp.opengl.*;

public class SGModelNode extends SGNode {

  protected Model model;

  public SGModelNode(String name, Model m) {
    super(name);
    model = m; 
  }

  public void draw(GL3 gl) {
    model.render(gl, worldTransform);
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

}