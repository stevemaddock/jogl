import gmaths.*;
import com.jogamp.opengl.util.texture.*;

 /**
 * This class stores the Material properties for a Mesh
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 */

public class Material implements Cloneable {
  
  // Note: if assigned, these cannot be changed. For a changeable value need to create
  // a new Vec3 using one of these.
  // Default is a fairly bright white colour with some specular.
  public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
  public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
  public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.5f, 0.5f, 0.5f);
  public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);

  public static final float DEFAULT_SHININESS = 32;

  private Vec3 ambient;
  private Vec3 diffuse;
  private Vec3 specular;
  private Vec3 emission;
  private float shininess;
  private Texture diffuseMap;
  private Texture specularMap;
  private Texture emissionMap;

  /**
   * Constructor. Sets attributes to default initial values.
   */    
  public Material() {
    this(new Vec3(DEFAULT_AMBIENT), new Vec3(DEFAULT_DIFFUSE), 
         new Vec3(DEFAULT_SPECULAR), new Vec3(DEFAULT_EMISSION), 
         null, null, null, DEFAULT_SHININESS);
  }

   /**
   * Constructor. Sets the ambient, diffuse, specular, emission and shininess values
   * 
   * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   */  
   
  public Material(Vec3 ambient, Vec3 diffuse) {
    this(ambient, diffuse, new Vec3(DEFAULT_SPECULAR), new Vec3(DEFAULT_EMISSION), 
         null, null, null, DEFAULT_SHININESS);
  }

  /**
   * Constructor. Sets the ambient, diffuse, specular and shininess values
   * 
   * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  specular   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  shininess   float value in the range 0.0..1.0.
   */  
   
  public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, float shininess) {
    this(ambient, diffuse, specular, new Vec3(DEFAULT_EMISSION), 
         null, null, null, shininess);
  }

  /**
   * Constructor. Sets the ambient, diffuse, specular, emission and shininess values
   * 
   * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  specular   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  emission   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  shininess   float value in the range 0.0..1.0.
   */  
   
   public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, Vec3 emission, float shininess) {
    this(ambient, diffuse, specular, emission, 
         null, null, null, shininess);
  }

  /**
   * Constructor. Sets the diffuseMap, specularMap, emissionMap and shininess values
   * 
   * @param  diffuseMap    texture map of diffuse values
   * @param  specularMap   texture map of specular values
   * @param  emissionMap   texture map of emission values
   * @param  shininess     float value in the range 0.0..1.0.
   */  
   
  public Material(Texture diffuseMap, Texture specularMap, Texture emissionMap, float shininess) {
    this(new Vec3(DEFAULT_AMBIENT), new Vec3(DEFAULT_DIFFUSE), 
         new Vec3(DEFAULT_SPECULAR), new Vec3(DEFAULT_EMISSION), 
         diffuseMap, specularMap, emissionMap, shininess);
  }
  
  /**
   * Constructor. Sets the ambient, diffuse, specular, emission and shininess values
   * 
   * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  specular   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  emission   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuseMap    texture map of diffuse values
   * @param  specularMap   texture map of specular values
   * @param  emissionMap   texture map of emission values
   * @param  shininess   float value in the range 0.0..1.0.
   */

  public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, Vec3 emission,
                  Texture diffuseMap, Texture specularMap, Texture emissionMap, 
                  float shininess) {
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.emission = emission;
    this.shininess = shininess;
    this.diffuseMap = diffuseMap;
    this.specularMap = specularMap;
    this.emissionMap = emissionMap;
  }

  /**
   * Sets the ambient value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
   */    
  public void setAmbient(float red, float green, float blue) {
    ambient.x = red;
    ambient.y = green;
    ambient.z = blue;
  }  
  
  /**
   * Sets the ambient value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the  3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */    
  public void setAmbient(Vec3 rgb) {
    setAmbient(rgb.x, rgb.y, rgb.z);
  }
  
  /**
   * Gets the ambient value (as a clone)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */  
  public Vec3 getAmbient() {
    return new Vec3(ambient);
  }

  /**
   * Sets the diffuse value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
  */  
  public void setDiffuse(float red, float green, float blue) {
    diffuse.x = red;
    diffuse.y = green;
    diffuse.z = blue;
  }
  
  /**
   * Sets the diffuse value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the  3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */      
  public void setDiffuse(Vec3 rgb) {
    setDiffuse(rgb.x, rgb.y, rgb.z);
  }

  /**
   * Gets the diffuse value (clone) (as used in Phong local reflection model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue
   */    
  public Vec3 getDiffuse() {
    return new Vec3(diffuse);
  }

  /**
   * Sets the specular value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
  */    
  public void setSpecular(float red, float green, float blue) {
    specular.x = red;
    specular.y = green;
    specular.z = blue;
  }

  /**
   * Sets the specular value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the first 3 values are the values for red, green and blue, 
                   in the range 0.0..1.0, and the last value is an alpha term, which is always 1.
   */    
  public void setSpecular(Vec3 rgb) {
    setSpecular(rgb.x, rgb.y, rgb.z);
  }
    
  /**
   * Gets the specular value (clone) (as used in Phong local reflection model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */  
  public Vec3 getSpecular() {
    return new Vec3(specular);
  }

  /**
   * Sets the emission value (as used in OpenGL lighting model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
   */    
  public void setEmission(float red, float green, float blue) {
    emission.x = red;
    emission.y = green;
    emission.z = blue;
  }
  
  /**
   * Sets the emission value (as used in OpenGL lighting model)
   * 
   * @param  rgb  vector of 3 values, where the 3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */    
  public void setEmission(Vec3 rgb) {
    setEmission(rgb.x, rgb.y, rgb.z);
  }

  /**
   * Gets the emission value (clone) (as used in OpenGL lighting model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */ 
  public Vec3 getEmission() {
    return new Vec3(emission);
  }
    
  /**
   * Sets the shininess value (as used in Phong local reflection model)
   * 
   * @param  shininess  the shininess value.
   */   
  public void setShininess(float shininess) {
    this.shininess = shininess;
  }
  
  /**
   * Gets the shininess value (as used in Phong local reflection model)
   * 
   * @return  the shininess value.
   */   
  public float getShininess() {
    return shininess;
  }

  public boolean diffuseMapExists() {
    return (diffuseMap!=null);
  }

  public void setDiffuseMap(Texture diffuseMap) {
    this.diffuseMap = diffuseMap;
  }

  public Texture getDiffuseMap() {
    return diffuseMap;
  }

  public boolean specularMapExists() {
    return (specularMap!=null);
  }

  public void setSpecularMap(Texture specularMap) {
    this.specularMap = specularMap;
  }

  public Texture getSpecularMap() {
    return specularMap;
  }

  public boolean emissionMapExists() {
    return (emissionMap!=null);
  }

  public void setEmissionMap(Texture emissionMap) {
    this.emissionMap = emissionMap;
  }

  public Texture getEmissionMap() {
    return emissionMap;
  }

  public Material clone() {
    Material cloned = new Material();
    cloned.ambient = new Vec3(this.ambient);
    cloned.diffuse = new Vec3(this.diffuse);
    cloned.specular = new Vec3(this.specular);
    cloned.emission = new Vec3(this.emission);
    cloned.shininess = this.shininess;
    cloned.diffuseMap = this.diffuseMap;
    cloned.specularMap = this.specularMap;
    cloned.emissionMap = this.emissionMap;
    return cloned;
  }

  public String toString() {
    return "a:"+ambient+", d:"+diffuse+", s:"+specular+", e:"+emission
            +", dm:"+diffuseMap+", sm:"+specularMap+", em:"+emissionMap
            +", shininess:"+shininess;
  }  

}