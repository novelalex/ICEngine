#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location=0) in vec3 inVertex;
layout (location=1) in vec3 inNormal;
layout (location=2) in vec2 inTexCoord;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;


layout (location=0) out vec2 fTexCoord;


void main() {
    // set color based on normalized vertex position
    gl_Position = projection * view * model * vec4(inVertex, 1.0);
    fTexCoord = inTexCoord;
}