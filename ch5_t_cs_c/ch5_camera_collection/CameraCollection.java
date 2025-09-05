import gmaths.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class CameraCollection {
  
  private Map<String,Camera> cameras;

  private Camera activeCamera;

  public CameraCollection() {
    cameras = new HashMap<String, Camera>();
  }

  public void add(String name, Camera c) {
    cameras.put(name, c);
  }

  public Camera get(String name) {
    return cameras.get(name);
  }

  public void setActiveCamera(Camera c) {
    //System.out.println("setActiveCamera ");
    activeCamera = c;
  }

  public Camera getActiveCamera() {
    //System.out.println("getActiveCamera ");
    return activeCamera;
  }

  public void setAllPerspectiveMatrix(Mat4 p) {
    // for (Map.Entry<String,Camera> entry : cameras.entrySet()) {
    //   entry.getValue().setPerspectiveMatrix(p);
    // }
    for (Camera value: cameras.values()) {
      value.setPerspectiveMatrix(p);
    }
  }

  public Collection<Camera> getCameras() {
    return cameras.values();
  }
}