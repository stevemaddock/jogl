import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class L05 extends JFrame  { //implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private L05_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    L05 b1 = new L05("L05");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public L05(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new L05_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);

    // JPanel p = new JPanel();
    //   JButton b = new JButton("smile");
    //   b.addActionListener(this);
    //   p.add(b);
    //   b = new JButton("frown");
    //   b.addActionListener(this);
    //   p.add(b);
    // this.add(p, BorderLayout.SOUTH);


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
  
  // public void actionPerformed(ActionEvent e) {
  //   if (e.getActionCommand().equalsIgnoreCase("smile")) {
  //     glEventListener.setSmile(true);
  //   }
  //   else if (e.getActionCommand().equalsIgnoreCase("frown")) {
  //     glEventListener.setSmile(false);
  //   }
  // }

}
