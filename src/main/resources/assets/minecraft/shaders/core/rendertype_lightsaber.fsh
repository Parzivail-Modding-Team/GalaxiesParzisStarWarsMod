#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform mat4 ModelViewMat;
uniform float FogStart;
uniform float FogEnd;

in vec4 vertexColor;
in vec3 vertexPosition;
smooth in vec3 vertexNormal;
in vec4 vertexNormal4;

out vec4 fragColor;

void main() {
    if (gl_FrontFacing)
        discard;

//    vec3 v = (ModelViewMat * vec4(vertexPosition, 1.0)).xyz;
//    vec3 n = (ModelViewMat * vec4(vertexNormal, 1.0)).xyz;

    vec3  N      = normalize(vertexNormal);
    vec3  L      = (ModelViewMat * vec4(vertexPosition, 0.0)).xyz;
    float NdotL  = dot(N, L);

    fragColor = vec4(vec3(NdotL), 1.0);

//    fragColor = vec4(vec3(dot(vec3(0.0, 0.0, 1.0), vertexNormal)), 1.0);
}
