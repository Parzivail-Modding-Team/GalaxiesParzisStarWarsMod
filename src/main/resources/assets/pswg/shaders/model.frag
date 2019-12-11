#version 330 core

uniform vec3 lightPos;
uniform sampler2D texRandom;
uniform sampler2D texModel;

in vec3 fragNormal;
in vec2 fragTexCoord;

out vec4 color;

void main()
{
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos);// light very far away, use direction only. If light has position: normalize(lightPos - fragPos)
    float diffuse = max(dot(norm, lightDir), 0.0);
    float ambient = 0.5;

    vec4 samp = texture(texModel, fragTexCoord);
    color = vec4(samp.rgb * clamp(ambient + diffuse, 0, 1), samp.a);

    vec2 resolution = vec2(1024., 1024.);
    vec4 noise = texture(texRandom, gl_FragCoord.xy / resolution.xy);
    color += vec4(vec3((noise - 0.5) / 255.0), 0.0);
}
