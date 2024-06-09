#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location=0) in vec3 inVertex;
layout (location=1) in vec3 inNormal;
layout (location=2) in vec2 inTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec3 incident;
out vec3 normal;
//out vec2 textureCoords; 

void main() {
    //textureCoords = inTexCoord;
    vec3 vertexPositionWorldSpace = (inVertex).xyz; // swizzle! out the w component
    vec3 cameraPos = vec3(0,0,0);
    incident = normalize(vertexPositionWorldSpace);
    mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
    normal = normalize(normalMatrix * inNormal);
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(inVertex, 1.0);
}