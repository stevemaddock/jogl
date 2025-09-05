import java.awt.*;
import java.awt.event.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class V06 extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private V06_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private CameraControl cameraControl;

  public static void main(String[] args) {
    V06 b1 = new V06("V06");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public V06(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    
    cameraControl = new CameraControl(canvas); 
    
    glEventListener = new V06_GLEventListener(cameraControl);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(cameraControl);
    canvas.addKeyListener(cameraControl);
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
      p.add(cameraControl.getPanel()); // first row
      JPanel labels = new JPanel(); // second row start
      JLabel l1 = new JLabel("This line does nothing but show layout possibilities");
      l1.setOpaque(true);
      l1.setBackground(Color.yellow);
      labels.add(l1);
      JLabel l2 = new JLabel("Another nothing on the same line");
      l2.setOpaque(true);
      l2.setBackground(Color.orange);
      labels.add(l2);
      p.add(labels); // second row end
    this.add(p, BorderLayout.SOUTH);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });

    animator = new FPSAnimator(canvas, 60);
    animator.setUpdateFPSFrames(200, System.out);
    animator.start();
  }
  
}
