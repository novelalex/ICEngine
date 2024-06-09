#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) out vec4 fragColor;

in vec3 incident;
in vec3 normal;

uniform samplerCube tex;

void main() {
    vec3 reflected = reflect(incident, normal);
   	fragColor = texture(tex, reflected);
}