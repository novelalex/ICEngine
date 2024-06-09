#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location=0) in vec3 inVertex;
layout (location=1) in vec3 inNormal;
layout (location=2) in vec2 inTexCoord;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;


layout(location = 0) out vec3 vertNormal;
layout(location = 1) out vec3 lightDir;
layout(location = 2) out vec3 eyeDir;
layout(location = 3) out vec2 textureCoords; 


void main() {
    textureCoords = inTexCoord;
    mat3 normalMatrix = mat3(transpose(inverse(model)));
    vertNormal = normalize(normalMatrix * inNormal);
    vec3 vertPos = vec3(view * model * vec4(inVertex, 1.0));
    vec3 vertDir = normalize(vertPos);
    eyeDir = -vertDir;
    lightDir = normalize(vec3(0, 0, 0) - vertPos);
    gl_Position = projection * view * model * vec4(inVertex, 1.0);
}