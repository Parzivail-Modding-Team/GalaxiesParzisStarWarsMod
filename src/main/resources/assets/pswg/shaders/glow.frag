uniform sampler2D bgl_RenderedTexture;
uniform int time; // Passed in, see ShaderHelper.java

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0]);
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    
    gl_FragColor = vec4(color.r - ((sin(time / 10f) / 10) + 0.1), color.g - ((sin(time / 10f) / 10) + 0.1), color.b - ((sin(time / 10f) / 10) + 0.1), 1);
}