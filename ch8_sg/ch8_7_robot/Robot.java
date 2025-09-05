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
  private SGTransformNode robotMoveTranslate, leftArmRotate, rightArmRotate;
   
  public Robot(GL3 gl, Camera cameraIn, Light lightIn, 
               Texture diffuseHeadSmile,
               Texture diffuseC1, Texture specularC1, 
               Texture diffuseC2, Texture specularC2) {

    this.camera = cameraIn;
    this.light = lightIn;

    sphere = makeSphere(gl, diffuseHeadSmile);

    cube = makeCube(gl, diffuseC1, specularC1);
    cube2 = makeCube(gl, diffuseC2, specularC2);

    // robot
    
    float bodyHeight = 3f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;
    float headScale = 2f;
    float armLength = 3.5f;
    float armScale = 0.5f;
    float legLength = 3.5f;
    float legScale = 0.67f;
    
    robotRoot = new SGNameNode("root");
    robotMoveTranslate = new SGTransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    
    SGTransformNode robotTranslate = new SGTransformNode("robot transform",Mat4Transform.translate(0,legLength,0));
    
    // make pieces
    SGNameNode body = makeBody(gl, bodyWidth,bodyHeight,bodyDepth, cube);
    SGNameNode head = makeHead(gl, bodyHeight, headScale, sphere);
    SGNameNode leftArm = makeLeftArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    SGNameNode rightArm = makeRightArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    SGNameNode leftLeg = makeLeftLeg(gl, bodyWidth, legLength, legScale, cube);
    SGNameNode rightLeg = makeRightLeg(gl, bodyWidth, legLength, legScale, cube);

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

  private Model makeSphere(GL3 gl, Texture diffuse) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Mat4 modelMatrix = new Mat4(1);
    // Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", 
                                 "assets/shaders/fs_standard_d.txt");
    Material material = new Material();
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(null);
    material.setEmissionMap(null);
    Renderer renderer = new Renderer();
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture diffuse, Texture specular) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 modelMatrix = new Mat4(1);
    // modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", 
                                 "assets/shaders/fs_standard_ds.txt");
    Material material = new Material();
    material.setDiffuseMap(diffuse);
    material.setSpecularMap(specular);
    material.setEmissionMap(null);
    Renderer renderer = new Renderer();
    Model cube = new Model(name, mesh, modelMatrix, shader, material, renderer, light, camera);
    return cube;
  } 

  private SGNameNode makeBody(GL3 gl, float bodyWidth, float bodyHeight, float bodyDepth, Model cube) {
    SGNameNode body = new SGNameNode("body");
    Mat4 m = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode bodyTransform = new SGTransformNode("body transform", m);
    SGModelNode bodyShape = new SGModelNode("Cube(body)", cube);
    body.addChild(bodyTransform);
    bodyTransform.addChild(bodyShape);
    return body;
  }

  private SGNameNode makeHead(GL3 gl, float bodyHeight, float headScale, Model sphere) {
    SGNameNode head = new SGNameNode("head");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyHeight,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(180));  // as sphere u,v coordinates need rotating 180 degrees to bring texture to front 
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode headTransform = new SGTransformNode("head transform", m);
    SGModelNode headShape = new SGModelNode("Sphere(head)", sphere);
    head.addChild(headTransform);
    headTransform.addChild(headShape);
    return head;
  }

  private SGNameNode makeLeftArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    SGNameNode leftArm = new SGNameNode("left arm");
    SGTransformNode leftArmTranslate = new SGTransformNode("leftarm translate", 
                                          Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
    // leftArmRotate is a class attribute with a transform that changes over time
    leftArmRotate = new SGTransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode leftArmScale = new SGTransformNode("leftarm scale", m);
    SGModelNode leftArmShape = new SGModelNode("Cube(left arm)", cube);
    leftArm.addChild(leftArmTranslate);
    leftArmTranslate.addChild(leftArmRotate);
    leftArmRotate.addChild(leftArmScale);
    leftArmScale.addChild(leftArmShape);
    return leftArm;
  }

  private SGNameNode makeRightArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    SGNameNode rightArm = new SGNameNode("right arm");
    SGTransformNode rightArmTranslate = new SGTransformNode("rightarm translate", 
                                          Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0));
    rightArmRotate = new SGTransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode rightArmScale = new SGTransformNode("rightarm scale", m);
    SGModelNode rightArmShape = new SGModelNode("Cube(right arm)", cube);
    rightArm.addChild(rightArmTranslate);
    rightArmTranslate.addChild(rightArmRotate);
    rightArmRotate.addChild(rightArmScale);
    rightArmScale.addChild(rightArmShape);
    return rightArm;
  }

  private SGNameNode makeLeftLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    SGNameNode leftLeg = new SGNameNode("left leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode leftLegTransform = new SGTransformNode("leftleg transform", m);
    SGModelNode leftLegShape = new SGModelNode("Cube(leftleg)", cube);
    leftLeg.addChild(leftLegTransform);
    leftLegTransform.addChild(leftLegShape);
    return leftLeg;
  }

  private SGNameNode makeRightLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    SGNameNode rightLeg = new SGNameNode("right leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    SGTransformNode rightLegTransform = new SGTransformNode("rightleg transform", m);
    SGModelNode rightLegShape = new SGModelNode("Cube(rightleg)", cube);
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

  public float getMaxSwingTime() {
    return -1f;
  }

  public void loweredArms() {
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();
  }

  public void forwardArms() {
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(90));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(90));
    rightArmRotate.update();
  }

  public void dispose(GL3 gl) {
    // sphere.dispose(gl);
    // cube.dispose(gl);
    // cube2.dispose(gl);
  }
}