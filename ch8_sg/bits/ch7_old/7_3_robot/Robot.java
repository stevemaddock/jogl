import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

 /**
 * This class stores the Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Robot {

  private Camera camera;
  private Light light;

  private Model sphere, cube, cube2;

  private SGNode robotRoot;
  private float xPosition = 0;
  private TransformNode robotMoveTranslate, leftArmRotate, rightArmRotate;
   
  public Robot(GL3 gl, Camera cameraIn, Light lightIn, Texture t1, Texture t2, Texture t3, Texture t4, Texture t5, Texture t6) {

    this.camera = cameraIn;
    this.light = lightIn;

    sphere = makeSphere(gl, t1,t2);

    cube = makeCube(gl, t3,t4);
    cube2 = makeCube(gl, t5,t6);

    // robot
    
    float bodyHeight = 3f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;
    float headScale = 2f;
    float armLength = 3.5f;
    float armScale = 0.5f;
    float legLength = 3.5f;
    float legScale = 0.67f;
    
    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    
    TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,legLength,0));
    
    // make pieces
    NameNode body = makeBody(gl, bodyWidth,bodyHeight,bodyDepth, cube);
    NameNode head = makeHead(gl, bodyHeight, headScale, sphere);
    NameNode leftArm = makeLeftArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    NameNode rightArm = makeRightArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    NameNode leftLeg = makeLeftLeg(gl, bodyWidth, legLength, legScale, cube);
    NameNode rightLeg = makeRightLeg(gl, bodyWidth, legLength, legScale, cube);
    
    //Once all the pieces are created, then the whole robot can be created.
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(body);
          body.addChild(head);
          body.addChild(leftArm);
          body.addChild(rightArm);
          body.addChild(leftLeg);
          body.addChild(rightLeg);
    
    robotRoot.update();  // IMPORTANT - don't forget this

  }

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model cube = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return cube;
  } 

  private NameNode makeBody(GL3 gl, float bodyWidth, float bodyHeight, float bodyDepth, Model cube) {
    NameNode body = new NameNode("body");
    Mat4 m = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode bodyTransform = new TransformNode("body transform", m);
    ModelNode bodyShape = new ModelNode("Cube(body)", cube);
    body.addChild(bodyTransform);
    bodyTransform.addChild(bodyShape);
    return body;
  }
    
  private NameNode makeHead(GL3 gl, float bodyHeight, float headScale, Model sphere) {
    NameNode head = new NameNode("head"); 
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode headTransform = new TransformNode("head transform", m);
    ModelNode headShape = new ModelNode("Sphere(head)", sphere);
    head.addChild(headTransform);
    headTransform.addChild(headShape);
    return head;
  }

  private NameNode makeLeftArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    NameNode leftArm = new NameNode("left arm");
    TransformNode leftArmTranslate = new TransformNode("leftarm translate", 
                                          Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
    // leftArmRotate is a class attribute with a transform that changes over time
    leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    ModelNode leftArmShape = new ModelNode("Cube(left arm)", cube);
    leftArm.addChild(leftArmTranslate);
    leftArmTranslate.addChild(leftArmRotate);
    leftArmRotate.addChild(leftArmScale);
    leftArmScale.addChild(leftArmShape);
    return leftArm;
  }

  private NameNode makeRightArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    NameNode rightArm = new NameNode("right arm");
    TransformNode rightArmTranslate = new TransformNode("rightarm translate", 
                                          Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0));
    rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightArmScale = new TransformNode("rightarm scale", m);
    ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube);
    rightArm.addChild(rightArmTranslate);
    rightArmTranslate.addChild(rightArmRotate);
    rightArmRotate.addChild(rightArmScale);
    rightArmScale.addChild(rightArmShape);
    return rightArm;
  }

  private NameNode makeLeftLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    NameNode leftLeg = new NameNode("left leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftLegTransform = new TransformNode("leftleg transform", m);
    ModelNode leftLegShape = new ModelNode("Cube(leftleg)", cube);
    leftLeg.addChild(leftLegTransform);
    leftLegTransform.addChild(leftLegShape);
    return leftLeg;
  }

  private NameNode makeRightLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    NameNode rightLeg = new NameNode("right leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightLegTransform = new TransformNode("rightleg transform", m);
    ModelNode rightLegShape = new ModelNode("Cube(rightleg)", cube);
    rightLeg.addChild(rightLegTransform);
    rightLegTransform.addChild(rightLegShape);
    return rightLeg;
  }

  public void render(GL3 gl) {
    robotRoot.draw(gl);
  }

  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }

  // only does left arm
  public void updateAnimation(double elapsedTime) {
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    leftArmRotate.update();
  }

  public void loweredArms() {
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();
  }

  public void raisedArms() {
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightArmRotate.update();
  }

  public void dispose(GL3 gl) {
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
}