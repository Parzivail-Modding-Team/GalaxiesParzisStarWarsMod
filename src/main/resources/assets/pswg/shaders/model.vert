#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

out vec3 fragNormal;
out vec2 fragTexCoord;

out mat4 modelview;
out mat4 proj;

uniform mat4 mv;
uniform mat4 p;

void main()
{
    fragNormal = normalize(normal);
    fragTexCoord = texCoord;

    modelview = mv;
    proj = p;

    mat4 MVP = p*mv;
    gl_Position =  MVP * vec4(position, 1.);
}
