import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class V04 extends JFrame implements ActionListener{
  
  private static final long serialVersionUID = 1L;
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private V04_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    V04 b1 = new V04("V04");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public V04(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    glEventListener = new V04_GLEventListener();
    canvas.addGLEventListener(glEventListener);
    getContentPane().add(canvas, BorderLayout.CENTER);

    JPanel p = new JPanel();
      JButton b = new JButton("camera 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("camera 2");
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
    if (e.getActionCommand().equalsIgnoreCase("camera 1")) {
      glEventListener.setCamera(1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("camera 2")) {
      glEventListener.setCamera(2);
    }
  }
}