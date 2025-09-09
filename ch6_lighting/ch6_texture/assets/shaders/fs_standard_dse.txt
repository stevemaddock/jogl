#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform vec3 viewPos;
uniform sampler2D diffuse_texture;
uniform sampler2D specular_texture;
uniform sampler2D emission_texture;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};

uniform Light light;  

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
}; 
  
uniform Material material;

void main() {
  // ambient
  vec3 ambient = light.ambient * vec3(texture(diffuse_texture, aTexCoord));

  // diffuse
  vec3 norm = normalize(aNormal);
  vec3 lightDir = normalize(light.position - aPos);  
  float diff = max(dot(norm, lightDir), 0.0);
  vec3 diffuse = light.diffuse * diff * vec3(texture(diffuse_texture, aTexCoord)); 
  
  // specular 
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);  
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  vec3 specular = light.specular * spec * vec3(texture(specular_texture, aTexCoord));

  vec3 emission = vec3(texture(emission_texture, aTexCoord));

  vec3 result = ambient + diffuse + specular + emission;
  fragColor = vec4(result, 1.0);
}