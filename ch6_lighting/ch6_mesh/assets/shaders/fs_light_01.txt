#version 330 core

uniform vec3 lightColor;  
 
out vec4 fragColor;

void main() {
  fragColor = vec4(lightColor, 1.0);
  // fragColor = vec4(1.0f);
}