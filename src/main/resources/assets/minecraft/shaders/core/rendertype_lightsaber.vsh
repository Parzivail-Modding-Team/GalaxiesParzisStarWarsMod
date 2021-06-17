#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec3 vertexPosition;
smooth out vec3 vertexNormal;
out vec4 vertexNormal4;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
    vertexPosition = Position;
    vertexNormal = mat3(ModelViewMat) * Normal;
    vertexNormal4 = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
