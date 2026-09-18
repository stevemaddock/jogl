import java.awt.*;
import java.awt.event.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class SG04 extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private SG04_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    SG04 b1 = new SG04("SG04");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public SG04(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new SG04_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);

    JPanel p = new JPanel();
      JButton b = new JButton("F angle 0");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("F angle 180");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Cuboid angle 0");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Cuboid angle 45");
      b.addActionListener(this);
      p.add(b);
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
    animator.start();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("F angle 0")) {
      glEventListener.setFAngle(0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("F angle 180")) {
      glEventListener.setFAngle(180);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Cuboid angle 0")) {
      glEventListener.setCAngle(0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Cuboid angle 45")) {
      glEventListener.setCAngle(45);
    }
  }
}
