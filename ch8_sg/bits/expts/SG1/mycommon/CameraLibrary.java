package mycommon;

import gmaths.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class CameraLibrary {
  
  private Map<String,Camera> cameras;

  private Camera currentCamera;

  public CameraLibrary() {
    cameras = new HashMap<String, Camera>();
  }

  public void add(String name, Camera c) {
    cameras.put(name, c);
  }

  public Camera get(String name) {
    return cameras.get(name);
  }

  public void setCurrentCamera(Camera c) {
    //System.out.println("setCurrentCamera ");
    currentCamera = c;
  }

  public Camera getCurrentCamera() {
    //System.out.println("getCurrentCamera ");
    return currentCamera;
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