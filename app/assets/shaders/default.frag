#version 450
#extension GL_ARB_separate_shader_objects : enable

in vec4 fColor;
out vec4 color;

void main() {
    color = fColor;
}