import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
                                    // The list of import statements could be shortened by replacing some of the
                                    // import statements using wildcards, e.g. 
                                    // import java.awt.*;
                                    // import java.awt.event.*;
                                    // import com.jogamp.opengl.*;
                                    // Later examples will use wildcards.

public class A01 extends JFrame {   // A subclass of JFrame.

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
                                    // dimension is declared as static so it can be used in the main method,
                                    // which is static.
  private GLCanvas canvas;          // The canvas that will be drawn on.
  private GLEventListener glEventListener;
                                    // The listener to handle GL events
  private FPSAnimator animator; 
                                    // animator is declared as an attribute of the class so it can be accessed
                                    // and stopped from within the window closing event handler.
  
  public static void main(String[] args) {
    A01 f = new A01("A01");         // Create a subclass of JFrame.
    f.getContentPane().setPreferredSize(dimension);
                                    // setPreferredSize() is used for the content pane. When setPreferredSize is
                                    // used, must remember to pack() the JFrame after all elements have been added.
                                    // Note that the JFrame will be bigger, as the borders and title bar are added.
    f.pack();                       // Without pack(), the use of setPreferredSize() would 
                                    // result in a 0x0 canvas size, as nothing is yet drawn on it.
                                    // Alternative is to use f.setSize(dimension); rather than setPreferredSize and pack.
    f.setVisible(true);             // Finally, set the JFrame to be visible.
  }

  public A01(String textForTitleBar) {
    super(textForTitleBar);        // Call the superclass constructure to set the text for the title bar.
                                   
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
                                    // The desktop OpenGL core profile 3.x, with x >= 1
                                    
    canvas = new GLCanvas(glcapabilities);
                                    // Create the canvas for drawing on.
    glEventListener = new A01_GLEventListener();						
    canvas.addGLEventListener(glEventListener);
                                    // Add the GLEventListener to the canvas.
                                    // A separate class is used to implement the GLEventListener interface,
                                    // which is where all the OpenGL action happens.
    getContentPane().add(canvas, BorderLayout.CENTER);
                                    // Because a JFrame is used, the child (i.e. the canvas) must be added to the
                                    // content pane. The canvas is added to the centre, using the BorderLayout manager.
                                    // In later examples, we will add buttons near to the borders of the JFrame.
    addWindowListener(new WindowAdapter() {
                                    // Use an anonymous inner class to handle the window closing event.
      public void windowClosing(WindowEvent e) {
        animator.stop();            // Clean up before exiting.
        remove(canvas);
        dispose();                  // equivalent to this.dispose(); - disposes of the JFrame
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
                                    // Using an FPSAnimator, the aim is to redraw the canvas 60 times a second.
                                    // However, this will depend on how long it takes to draw the canvas.
    animator.start();               // Start the FPSAnimator.
  }
  
}