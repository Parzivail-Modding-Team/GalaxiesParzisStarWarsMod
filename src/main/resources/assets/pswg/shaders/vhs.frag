#version 120

uniform sampler2D iChannel0;
uniform sampler2D iChannel1;
uniform vec3 iResolution;
uniform float time;

float rand(vec2 co)
{
    highp float a = 12.9898;
    highp float b = 78.233;
    highp float c = 43758.5453;
    highp float dt= dot(co.xy ,vec2(a,b));
    highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

float makestep(float f, float steps)
{
    return floor(f * steps) / steps;
}

vec4 composite_shift(sampler2D sam, vec2 uv, float amount)
{
    vec4 colorA = texture2D(sam, uv) / 2.;
    vec4 colorB = texture2D(sam, uv + vec2(0, amount)) / 2.;

    return colorA + colorB;
}

vec4 tracking_init_1(sampler2D from, sampler2D to, vec2 uv)
{
    vec4 color;

    // frame bleed
    if (uv.y < 0.6)
		color = texture2D(from, uv);
    else
		color = texture2D(to, uv);

    // desaturate
    float avg = (color.r + color.b + color.g) / 3.;
    color = vec4(avg, avg, avg, color.a);

    // tracking init jitter
    if (uv.y > 0.15 && uv.y < 0.25)
    {
        float rHere = rand(vec2(makestep(uv.x, 60.), makestep(uv.y, 300.)));
        if (rHere > 0.9)
            color = vec4((rHere - 0.8) * 5.);
    }
    return color;
}

vec4 tracking_init_2(sampler2D from, sampler2D to, vec2 uv)
{
    // skew
    uv.x += uv.y * 3.;

    vec4 color = texture2D(to, uv);

    // desaturate
    float avg = (color.r + color.b + color.g) / 3.;
    color = mix(vec4(avg, avg, avg, color.a), color, 0.2);

    // tracking init mono jitter
    if (uv.y > 0.15 && uv.y < 0.25)
    {
        float rHere = rand(vec2(makestep(uv.x, 60.), makestep(uv.y, 300.)));
        if (rHere > 0.9)
            color = vec4((rHere - 0.8) * 5.);
    }

    // tracking init rgb jitter
    if (uv.y > 0.1 && uv.y < 0.2)
    {
        float rHere = rand(vec2(makestep(uv.x, 60.), makestep(uv.y, 300.)));

        float r = rand(vec2(makestep(uv.x + 1., 60.), makestep(uv.y + 1., 300.)));
        float g = rand(vec2(makestep(uv.x + 2., 60.), makestep(uv.y + 2., 300.)));
        float b = rand(vec2(makestep(uv.x + 3., 60.), makestep(uv.y + 3., 300.)));
        if (rHere > 0.9)
            color = vec4(r, g, b, 1);
    }

    return color;
}

vec4 tracking_init_3(sampler2D from, sampler2D to, vec2 uv)
{
    // roll
    uv.y += 0.3;

    if (uv.y > 1.)
        uv.y -= 1.;

    // scanline splitting
    vec4 color = composite_shift(to, uv, 0.05);

    // desaturate
    float avg = (color.r + color.b + color.g) / 3.;
    color = mix(vec4(avg, avg, avg, color.a), color, 0.4);

    // tracking init rgb jitter
    if (uv.y > 0.85 && uv.y < 0.95)
    {
        float rHere = rand(vec2(makestep(uv.x, 60.), makestep(uv.y, 300.)));

        float r = rand(vec2(makestep(uv.x + 1., 60.), makestep(uv.y + 1., 300.)));
        float g = rand(vec2(makestep(uv.x + 2., 60.), makestep(uv.y + 2., 300.)));
        float b = rand(vec2(makestep(uv.x + 3., 60.), makestep(uv.y + 3., 300.)));
        if (rHere > 0.9)
            color = vec4(r, g, b, 1);
    }

    return color;
}

vec4 tracking_init_4(sampler2D from, sampler2D to, vec2 uv)
{
    // roll
    uv.y += 0.2;

    // scanline splitting
    vec4 color = composite_shift(to, uv, 0.1);

    // desaturate
    float avg = (color.r + color.b + color.g) / 3.;
    color = mix(vec4(avg, avg, avg, color.a), color, 0.8);

    return color;
}

vec4 tracking_init_5(sampler2D from, sampler2D to, vec2 uv)
{
    // scanline splitting
    vec4 color = composite_shift(to, uv, 0.15);

    return color;
}

vec2 top_tracking_warp(vec2 randvec, vec2 uv)
{
    float mag = 0.2 + (rand(uv + randvec) / 50.);
	uv.x -= clamp(((1. / pow(1. - uv.y + 0.01, 2.) - 1.) / 100000.) * mag, 0., 1.);
    return uv;
}

vec2 ntsc_interlace_jitter(vec2 randvec, vec2 uv)
{
    int uvY = int(uv.y * 525.);
    if (mod(float(uvY), 2.) == 0.)
        uv.x += 0.001 * rand(randvec);
    return uv;
}

void main()
{
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
    vec2 now = vec2(time / 2., time * 2.);

    if (time < 0.18)
    {
        //uv.y = 1. - uv.y;

        // steps: tracking_init_X [1, 2, 3, 4, 5] -> final color
        // channel 0 is the "logo" or whatever before the main UI
        // channel 1 is the main UI

        vec4 color;

        // frame length (s)
        float frameLen = 0.03;

        // start at the intro
        color = tracking_init_1(iChannel1, iChannel0, uv);

        // clip into the UI
        if (time > frameLen * 1.)
            color = tracking_init_2(iChannel1, iChannel0, uv);
        if (time > frameLen * 2.)
            color = tracking_init_3(iChannel1, iChannel0, uv);
        if (time > frameLen * 3.)
            color = tracking_init_4(iChannel1, iChannel0, uv);
        if (time > frameLen * 4.)
            color = tracking_init_5(iChannel1, iChannel0, uv);
        if (time > frameLen * 5.)
            color = texture2D(iChannel0, uv);

        gl_FragColor = color;
    }
    else
    {
        highp float magnitude = 0.;

        uv = top_tracking_warp(now, uv);

        uv = ntsc_interlace_jitter(now, uv);

        // Set up offset
        vec2 offsetRedUV = uv;
        offsetRedUV.x = uv.x + rand(vec2(time*0.03,uv.y*0.42)) * 0.0004;
        offsetRedUV.x += sin(rand(vec2(time*0.2, uv.y)))*magnitude;

        vec2 offsetGreenUV = uv;
        offsetGreenUV.x = uv.x + rand(vec2(time*0.004,uv.y*0.002)) * 0.0008;
        offsetGreenUV.x += sin(time*9.0)*magnitude;

        // Load Texture
        float r = texture2D(iChannel0, offsetRedUV).r;
        float g = texture2D(iChannel0, offsetGreenUV).g;
        float b = texture2D(iChannel0, uv).b;
        float a = texture2D(iChannel0, uv).a;

        gl_FragColor = vec4(r, g, b, a);
    }
}