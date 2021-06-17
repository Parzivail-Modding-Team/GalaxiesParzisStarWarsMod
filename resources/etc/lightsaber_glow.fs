#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec3 rgb2hsv(vec3 c)
{
	vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
	vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
	vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

	float d = q.x - min(q.w, q.y);
	float e = 1.0e-10;
	return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
	vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
	vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
	return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float getAlpha(float x)
{
	return clamp(1.0 - (x / 100.0 + 0.4) / (1.0 + exp(-0.3 * (x - 22.0))), 0., 1.);
}

float getSaturation(float x)
{
	return clamp((x / 400.0 + 0.76) / (1.0 + exp(-0.27 * (x - 10.0))), 0., 1.);
}

float getHue(float h, float x)
{
	return clamp(-0.06 * exp(-0.011 * pow(x - 6.0, 2.)) + h, 0., 1.);
}

void main( void )
{
	vec2 position = floor(gl_FragCoord.xy / vec2(1.0));

	vec3 bgColor = vec3(0.0);

	float hue = 0.61;

	float x = abs(position.x - 80.0);
	
	vec3 color = hsv2rgb(vec3(getHue(hue, x), getSaturation(x), 1.0));
	float alpha = getAlpha(x);

	gl_FragColor = vec4(mix(bgColor, color, alpha), 1.0);
}