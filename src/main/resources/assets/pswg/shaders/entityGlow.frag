#version 120
uniform int time; // Passed in, see ShaderHelper.java

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = vec2(gl_TexCoord[0]);

    // Time varying pixel color
    vec3 col = gl_Color.rgb;

    col = col + vec3(
        0.1 - rand(vec2(time,0.) + uv) * 0.1,
        0.5 - rand(vec2(time,0.) + uv) * 0.2,
        0.2 - rand(vec2(time,0.) + uv) * 0.1
        );

    // Output to screen
    gl_FragColor = vec4(col,1.0);
}