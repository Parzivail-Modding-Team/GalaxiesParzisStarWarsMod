#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    if (gl_FrontFacing && vertexColor.a < 1)
        discard;

    fragColor = vertexColor;
}
