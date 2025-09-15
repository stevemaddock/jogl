package mycommon;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JPanel;

import gmaths.*;

import com.jogamp.opengl.awt.GLCanvas;

// could extend camera to have static and moveable cameras using a boolean flag or abstract class and extend?

public class CameraControl implements ActionListener, MouseMotionListener, KeyListener {
  
  private static final String CAMERA1 = "camera1";
  private static final String CAMERA2 = "camera2";
  private static final String CAMERA3 = "camera3";

  private Point lastpoint;
  private CameraLibrary cameras;
  private GLCanvas canvas;

  public CameraControl(GLCanvas canvas) {
    this.canvas = canvas;
    cameras = new CameraLibrary();
    Camera camera1 = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    camera1.setPosition(new Vec3(4f,3f,15f));
    camera1.setTarget(new Vec3(0f,0f,0f));
    Camera camera2 = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    Camera camera3 = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    cameras.add(CAMERA1, camera1);
    cameras.add(CAMERA2, camera2);
    cameras.add(CAMERA3, camera3);
    cameras.setCurrentCamera(cameras.get(CAMERA1));
  }

  public JPanel getPanel() {
    JPanel p = new JPanel();
      JButton b = new JButton(CAMERA1);
      b.addActionListener(this);
      p.add(b);
      b = new JButton(CAMERA2);
      b.addActionListener(this);
      p.add(b);
      b = new JButton(CAMERA3);
      b.addActionListener(this);
      p.add(b);
    return p;
  }

  public CameraLibrary getCameraLibrary() {
    return cameras;
  }

  public void setCurrentCamera(String name) {
    cameras.setCurrentCamera(cameras.get(name));
  }

  public Camera getCurrentCamera() {
    return cameras.getCurrentCamera();
  }

  public void updateCameras(Mat4 p) {
    Collection<Camera> list = cameras.getCameras();
    for (Camera c: list) {
      c.setPerspectiveMatrix(p);
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase(CAMERA1)) {
      setCurrentCamera(CAMERA1);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase(CAMERA2)) {
      setCurrentCamera(CAMERA2);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase(CAMERA3)) {
      setCurrentCamera(CAMERA3);
      canvas.requestFocusInWindow();
    }
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
      cameras.getCurrentCamera().updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
  
  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    cameras.getCurrentCamera().keyboardInput(m);
  }

  public void keyReleased(KeyEvent e) {
  }

}