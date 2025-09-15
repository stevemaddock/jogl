import mycommon.*;
import animation.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class SG1 extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private SG1_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private CameraControl cameraControl;
  private static MyClock clock;

  public static void main(String[] args) {
    System.loadLibrary("renderdoc");   // added to use RenderDoc also need RenderDoc path on PATH environment variable
    clock = new MyClock();
    SG1 b1 = new SG1("SG1");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public SG1(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
     
    cameraControl = new CameraControl(canvas); 
    Another another = new Another();  // to see what is possible

    glEventListener = new SG1_GLEventListener(cameraControl, clock);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(cameraControl);
    canvas.addKeyListener(cameraControl); 
    canvas.addKeyListener(another);  // to see what is possible
    getContentPane().add(canvas, BorderLayout.CENTER);

    JPanel p = new JPanel();
    String buttonName = "pause";
    JButton b = new JButton(buttonName);
    b.setActionCommand(buttonName);
    b.addActionListener(e -> buttonAction(e));
    p.add(b);
    buttonName = "unPause";
    b = new JButton(buttonName);
    b.setActionCommand(buttonName);
    b.addActionListener(e -> buttonAction(e));
    p.add(b);

    //b = makeButton("pause", glEventListener.pause());
  
    // ButtonListener bl = new ButtonListener(glEventListener);
    // JPanel p = new JPanel();
    //   JButton b = new JButton("pause");
    //   b.addActionListener(bl);
    //   p.add(b);
    //   b = new JButton("start");
    //   b.addActionListener(bl);
    //   p.add(b);

    JPanel pMain = new JPanel();
      pMain.setLayout(new BoxLayout(pMain,BoxLayout.PAGE_AXIS));
      pMain.add(cameraControl.getPanel());
      pMain.add(p);
    this.add(pMain, BorderLayout.SOUTH);

    // could use Swing Key Bindings instead - https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html


    //this.add(cameraControl.getPanel(), BorderLayout.SOUTH);
    // where to add other interface controls if needed? 

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

  private void buttonAction(ActionEvent e) {
    String name = e.getActionCommand();
    if (name.equalsIgnoreCase("pause")) {
      clock.pause();
      System.out.println("pause");
    }
    else if (name.equalsIgnoreCase("unPause")) {
      clock.unPause();
      System.out.println("unPause");
    }
  }

}

class Another implements KeyListener {

  public Another() {

  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  System.out.println("left");  break;
      case KeyEvent.VK_S:  System.out.println("S");  break;
      case KeyEvent.VK_Z:  System.out.println("Z");  break;
    }
  }

  public void keyReleased(KeyEvent e) {
  }
}

// // class ButtonListener implements ActionListener {

// //   private SG1_GLEventListener glEventListener;

// //   public ButtonListener(SG1_GLEventListener glEventListener) {
// //     this.glEventListener = glEventListener;
// //   }

// //   public void actionPerformed(ActionEvent e) {
// //     if (e.getActionCommand().equalsIgnoreCase("pause")) {
// //       clock.pause();
// //     }
// //     else if (e.getActionCommand().equalsIgnoreCase("start")) {
// //       clock.unPause();
// //     }
// //   }
// }