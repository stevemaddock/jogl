import gmaths.*;

public class TestVec2 {
    
  public static void main(String[] args) {
    Vec2 v1 = new Vec2();
    Vec2 v2 = new Vec2();
    System.out.println("Vec2 v1 = " + v1);
    System.out.println("Vec2 v2 = " + v2);
    
    v1.x = 1;
    v2.y = 1;
    System.out.println("v1 = " + v1);
    System.out.println("v2 = " + v2);
    
    v1.add(v2);
    System.out.println("v1.add(v2);");
    System.out.println("v1 = " + v1);
    System.out.println("v2 = " + v2);
    
    Vec2 v3 = Vec2.add(new Vec2(-1f,0), v2);
    System.out.println("v3 = v1+(-1f,0) = " + v3);
    
    float m = v3.magnitude();
    System.out.println("v3.magnitude = " + m);
    
    Vec2 v4 = Vec2.normalize(v3);
    System.out.println("v4 = normalize(v3) = " + v4);
    
    v3.normalize();
    System.out.println("v3.normalize();");
    System.out.println("v3 = " + v3);
        
    Vec2 v5 = new Vec2(3.2f,5f);
    float d = v4.dotProduct(v5);
    System.out.println("v5 = new Vec2(3.2f,5f) = " + v5);
    System.out.println("d = v4.dotProduct(v5) = " + d);
  }
}