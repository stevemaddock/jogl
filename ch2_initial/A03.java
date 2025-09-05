import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class A03 extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private A03_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    A03 b1 = new A03("A03");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  /* the body of the constructor has been changed to show 
     an alternative way to add the canvas and window listener  */
  public A03(String textForTitleBar) {
    super(textForTitleBar);
    setUpCanvas();
    getContentPane().add(canvas, BorderLayout.CENTER);
    addWindowListener(new windowHandler());
    animator = new FPSAnimator(canvas, 60);
    animator.setUpdateFPSFrames(200, System.out);
    animator.start();
  }
  
  private void setUpCanvas() {
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    glEventListener = new A03_GLEventListener();
    canvas.addGLEventListener(glEventListener);
  }

  private class windowHandler extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      animator.stop();
      remove(canvas);
      dispose();
      System.exit(0);
    }
  }
}