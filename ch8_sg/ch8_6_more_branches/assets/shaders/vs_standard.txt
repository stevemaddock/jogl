#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

out vec3 aPos;
out vec3 aNormal;
out vec2 aTexCoord;

uniform mat4 model;
uniform mat4 mvpMatrix;

void main() {
  gl_Position = mvpMatrix * vec4(position, 1.0);
  aPos = vec3(model*vec4(position, 1.0f));
  aNormal = mat3(transpose(inverse(model))) * normal;  

  //alternative but would mean extra calculations for each vertec shader instance
  //mat4 normalMatrix = transpose(inverse(model));
  //vec3 norm = normalize(normal); // in case a normalised normal is not supplied
  //aNormal = mat3(normalMatrix) * norm;

  // pass texture on even if no textures used. Shader will ignore it.
  aTexCoord = texCoord;   
}