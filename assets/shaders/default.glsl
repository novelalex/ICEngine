#type vert
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

out vec4 fColor;

uniform vec3 mPos;

void main() {
    fColor = aColor;
    gl_Position = vec4(aPos + mPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;

uniform float mousePosX;
uniform float mousePosY;

void main() {
    color = fColor;
}

