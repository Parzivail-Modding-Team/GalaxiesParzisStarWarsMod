#version 120

uniform vec3 tint;

void main() { 
	gl_FragColor = vec4(gl_Color.rgb * tint, 1);
}