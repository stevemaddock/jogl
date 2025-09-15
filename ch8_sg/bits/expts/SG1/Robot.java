import gmaths.*;
import mycommon.*;
import mesh.*;
import scenegraph.*;
import animation.*;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

 /**
 * This class stores the Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Robot {

  private CameraLibrary cameras;
  private Light light;
  private DoubleTransition leftArmTransition, rightArmTransition;
  private Mat4Transition positionTransition;

  private Model sphere, cube, cube2;

  private SGNode robotRoot;
  private float xPosition = 0;
  private TransformNode robotPosition, leftArmRotate, rightArmRotate;
   
  public Robot(GL3 gl, CameraLibrary cameras, Light lightIn, TextureLibrary textures) {

    this.cameras = cameras;
    this.light = lightIn;

    // robot will be made of 3 models - sphere and 2 cubes
    // each cube has a different texture
    // sphere is the head, cube is the body and legs, cube2 is the arms

    sphere = makeSphere(gl, "sphere", textures.get("jade_diffuse"), 
                            textures.get("jade_specular"),
                            textures.get("emission"));
    cube = makeCube(gl, "cube1", textures.get("container_diffuse"), 
                        textures.get("container_specular"),
                        textures.get("emission"));
    cube2 = makeCube(gl, "cube2", textures.get("watt_diffuse"), 
                         textures.get("watt_specular"),
                         textures.get("emission")); 

    // robot dimensions
    float bodyHeight = 3f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;
    float headScale = 2f;
    float armLength = 3.5f;
    float armScale = 0.5f;
    float legLength = 3.5f;
    float legScale = 0.67f;
    
    // robot root node
    robotRoot = new NameNode("root");

    // translation node below root node will move the whole robot around the floor
    robotPosition = new TransformNode("robot transform",
      Mat4Transform.translate(xPosition,0,0));
    positionTransition = makePositionTransition();

    // translation node to move body of the robot above the floor
    TransformNode translateBodyAboveFloor = new TransformNode("robot transform",
      Mat4Transform.translate(0,legLength,0));

    // make pieces
    NameNode body = makeBody(gl, bodyWidth,bodyHeight,bodyDepth, cube);
    NameNode head = makeHead(gl, bodyHeight, headScale, sphere);
    NameNode leftArm = makeLeftArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    NameNode rightArm = makeRightArm(gl, bodyWidth, bodyHeight, armLength, armScale, cube2);
    NameNode leftLeg = makeLeftLeg(gl, bodyWidth, legLength, legScale, cube);
    NameNode rightLeg = makeRightLeg(gl, bodyWidth, legLength, legScale, cube);
    
    // transitions to control rotation of each arm
    leftArmTransition = makeLeftArmTransition();
    rightArmTransition = makeRightArmTransition();
    
    // transform nodes to attach the pieces to the body
    TransformNode attachHead = new TransformNode("attach head",
      Mat4.multiply(new Mat4(1), Mat4Transform.translate(0,bodyHeight,0)));
    TransformNode attachLeftArm = new TransformNode("attach left arm",
      Mat4.multiply(new Mat4(1), Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0)));
    TransformNode attachRightArm = new TransformNode("attach right arm",
      Mat4.multiply(new Mat4(1), Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0))); 
    TransformNode attachLeftLeg = new TransformNode("attach left leg",
      Mat4.multiply(new Mat4(1), Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0)));
    TransformNode attachRightLeg = new TransformNode("attach right leg",
      Mat4.multiply(new Mat4(1), Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0)));

    // Once all the pieces are created, then the whole robot can be created.
    robotRoot.addChild(robotPosition);
      robotPosition.addChild(translateBodyAboveFloor);
        translateBodyAboveFloor.addChild(body);
          body.addChild(attachHead);
            attachHead.addChild(head);
          body.addChild(attachLeftArm);
            attachLeftArm.addChild(leftArm);
          body.addChild(attachRightArm);
            attachRightArm.addChild(rightArm);
          body.addChild(attachLeftLeg);
            attachLeftLeg.addChild(leftLeg);
          body.addChild(attachRightLeg);
            attachRightLeg.addChild(rightLeg);
    
    // Now update all the transformations stored in each node in the scene graph
    // using the parent child hierarchy.
    // For a ModelNode, the transformation matrix stored in the node is used
    // when calling the Model.render(gl, transform).
    robotRoot.update();  // IMPORTANT - don't forget this

  }
  
  private Model makeSphere(GL3 gl, String name, Texture t1, Texture t2, Texture t3) {
    // use same name for transition so can be connected to each other
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    // Slight disconnect here as the modelMatrix is used when Model.render() is called
    // but ignored when ModelNode.draw() is called which instead uses the worldTransform
    // stored in the ModelNode, i.e. Model.render(gl, worldTransform).
    // Thus this modelMatrix could be set to the identity matrix.
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    // Uses the texture based material shader fs_standard_3t.txt
    // so requires 3 textures: diffuse, specular and emission
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", 
                                 "assets/shaders/fs_standard_3t.txt");
    Material material = new Material();
    material.setDiffuseMap(t1);
    material.setSpecularMap(t2);
    material.setEmissionMap(t3);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, cameras);
  }

  private Model makeCube(GL3 gl, String name, Texture t1, Texture t2, Texture t3) {
    // use same name for transition so can be connected to each other
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    //Mat4 modelMatrix = Mat4Transform.scale(4,4,4);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", 
                                 "assets/shaders/fs_standard_3t.txt");
    Material material = new Material();
    material.setDiffuseMap(t1);
    material.setSpecularMap(t2);
    material.setEmissionMap(t3);
    Renderer renderer = new Renderer();
    return new Model(name, mesh, modelMatrix, shader, material, renderer, light, cameras);
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
     // leftArmRotate is a class attribute with a transform that changes over time
    leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    ModelNode leftArmShape = new ModelNode("Cube(left arm)", cube);
    leftArm.addChild(leftArmRotate);
    leftArmRotate.addChild(leftArmScale);
    leftArmScale.addChild(leftArmShape);
    return leftArm;
  }

  private NameNode makeRightArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    NameNode rightArm = new NameNode("right arm");
    rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightArmScale = new TransformNode("rightarm scale", m);
    ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube);
    rightArm.addChild(rightArmRotate);
    rightArmRotate.addChild(rightArmScale);
    rightArmScale.addChild(rightArmShape);
    return rightArm;
  }

  private NameNode makeLeftLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    NameNode leftLeg = new NameNode("left leg");
    Mat4 m = new Mat4(1);
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
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightLegTransform = new TransformNode("rightleg transform", m);
    ModelNode rightLegShape = new ModelNode("Cube(rightleg)", cube);
    rightLeg.addChild(rightLegTransform);
    rightLegTransform.addChild(rightLegShape);
    return rightLeg;
  }

  // Sequence of values for the left arm rotation
  private DoubleTransition makeLeftArmTransition() {
    double[] times2 = {1, 2.0, 5.5, 8.5};
    List<Double> values = Arrays.asList(180.0,130.0,250.0,180.0);
    DoubleTransition mt = new DoubleTransition(0.0, times2, values, true);
    return mt;
  }

  // Sequence of values for the right arm rotation
  private DoubleTransition makeRightArmTransition() {
    double[] times2 = {1, 2.0, 5.5, 8.5};
    List<Double> values = Arrays.asList(180.0,250.0,130.0,180.0);
    DoubleTransition mt = new DoubleTransition(0.0, times2, values, true);
    return mt;
  }

  // Sequence of values for the robot position
  private Mat4Transition makePositionTransition() {
    double[] times2 = {1, 2.0, 5.5, 8.5};
    List<Mat4> values = Arrays.asList(
      Mat4Transform.translate(0,0,0),
      Mat4Transform.translate(4,0,0),
      Mat4Transform.translate(-4,0,0),
      Mat4Transform.translate(0,0,0));
    Mat4Transition mt = new Mat4Transition(2.0, times2, values, true);
    return mt;
  }

  // Some of the transforms in the scene graph are updated over time
  // by calling the update method in the Robot class.
  public void update(double t) {
    Double d = leftArmTransition.get(t);
    float a = d.floatValue();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(a));
    leftArmRotate.update();

    d = rightArmTransition.get(t);
    a = d.floatValue();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(a));
    rightArmRotate.update();

    Mat4 m = positionTransition.get(t);
    robotPosition.setTransform(m);
    robotPosition.update();
  }

  // starts at the robot's root node and traverses the scene graph
  // calling the draw method in each node.
  // When a ModelNode is reached, the Model.render() method is called
  // with the model's worldTransform matrix.
  public void render(GL3 gl) {
    robotRoot.draw(gl);
  }

}