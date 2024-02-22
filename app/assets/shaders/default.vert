#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location=0) in vec3 inVertex;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec4 fColor;

void main() {
    // set color based on normalized vertex position
    gl_Position = projection * view * model * vec4(inVertex, 1.0);
    fColor = vec4(normalize(inVertex), 1.0);
    
}