#version 330 core

#define saturation 80.0
#define impulseResponseSize 15

#define grainAmplitude 0.0
#define maskSize 2.0
#define scanlineSize 2.0
#define jitterChance 0.02

// original 0.25
#define ntscFreqColor 0.25

// original 0.125
#define ntscFreqLuma 0.125

// original 0.02
#define ntscFreqGrayscale 0.02

#define pi 3.14159265
#define tau 6.28318531

out vec4 FragColor;

uniform vec3 iResolution;
uniform sampler2D iChannel0;
uniform float time;
const bool enabled = true;

const mat3 yiq2rgb = mat3(1.000, 1.000, 1.000,
                    0.956,-0.272,-1.106,
                    0.621,-0.647, 1.703);

const mat3 rgb2yiq = mat3(0.299, 0.596, 0.211,
                    0.587,-0.274,-0.523,
                    0.114,-0.322, 0.312);

float rand(vec2 p)  // replace this by something better
{
	p *= 0.00005;
    p = 50.*fract(p*0.3183099 + vec2(0.71,0.113));
    return fract(p.x*p.y*(p.x+p.y));
}

vec2 ntscInterlaceJitter(vec2 randvec, vec2 uv)
{
    int uvY = int(uv.y * iResolution.y / maskSize);
	float r = rand(randvec + vec2(0., uvY));
    if (r < jitterChance && mod(float(uvY), 2.) == 0.)
        uv.x -= (scanlineSize / iResolution.x);
    return uv;
}

//Complex multiply
vec2 cmul(vec2 a, vec2 b)
{
   return vec2((a.x * b.x) - (a.y * b.y), (a.x * b.y) + (a.y * b.x));
}

float sinc(float x)
{
	if (x == 0.)
		return 1.;
	float a = x*pi*0.98;
	return sin(a)/a;
}

//https://en.wikipedia.org/wiki/Window_function
float WindowBlackman(float a, int N, int i)
{
    float a0 = (1.0 - a) * 0.5;
    float a2 = a * 0.5;

	float a3 = pi * (float(i) / float(N - 1));
    float wnd = a0;
    wnd -= 0.5 * cos(2.0 * a3);
    wnd += a2 * cos(4.0 * a3);

    return wnd;
}

//FIR lowpass filter
//Fc = Cutoff freq., Fs = Sample freq., N = # of taps, i = Tap index
float Lowpass(float Fc, float Fs, int N, int i)
{
    float wc = 2. * (Fc/Fs);

    float wnd = WindowBlackman(0.16, N, i);

    return wc * wnd * sinc(wc * float(i - N/2));
}

//FIR bandpass filter
//Fa/Fb = Low/High cutoff freq., Fs = Sample freq., N = # of taps, i = Tap index
float Bandpass(float Fa, float Fb, float Fs, int N, int i)
{
    float wa = (Fa/Fs);
    float wb = (Fb/Fs);

    float wnd = WindowBlackman(0.16, N, i);

	float a = 2 * float(i - N/2);
    return 2. * (wb-wa) * wnd * (sinc(wb * a) - sinc(wa * a));
}

//Complex oscillator, Fo = Oscillator freq., Fs = Sample freq., n = Sample index
vec2 Oscillator(float Fo, float Fs, float n)
{
    float phase = (tau*Fo*floor(n))/Fs;
    return vec2(cos(phase),sin(phase));
}

vec4 getTexture(vec2 fragCoord)
{
    return texture(iChannel0, fragCoord/iResolution.xy);
}

float getYiqSignal(vec2 fragCoord)
{
    float Fs = iResolution.x;
    float Fcol = Fs * ntscFreqColor;

    vec3 cRGB = getTexture(fragCoord).rgb;
    vec3 cYIQ = rgb2yiq * cRGB;

    vec2 cOsc = Oscillator(Fcol, Fs, fragCoord.x);

    return cYIQ.x + dot(cOsc, cYIQ.yz);
}

vec4 getLumaChromaSignal(vec2 fragCoord)
{
    float Fs = iResolution.x;
    float Fcol = Fs * ntscFreqColor;
    float Fcolbw = Fs * ntscFreqGrayscale;
    float Flumlp = Fs * ntscFreqLuma;
    float n = floor(fragCoord.x);

    float y_sig = 0.0;
    float iq_sig = 0.0;

    vec2 cOsc = Oscillator(Fcol, Fs, n);

    n += float(impulseResponseSize) * 0.5;

    //Separate luma(Y) & chroma(IQ) signals
    for(int i = 0;i < impulseResponseSize;i++)
    {
        int tpidx = impulseResponseSize - i - 1;
        float lp = Lowpass(Flumlp, Fs, impulseResponseSize, tpidx);
        float bp = Bandpass(Fcol - Fcolbw, Fcol + Fcolbw, Fs, impulseResponseSize, tpidx);

		float b = getYiqSignal(vec2(n - float(i), fragCoord.y));
        y_sig += b * lp;
        iq_sig += b * bp;
    }

    //Shift IQ signal down from Fcol to DC
    vec2 iq_sig_mix = cmul(vec2(iq_sig, 0), cOsc);

    return vec4(y_sig, iq_sig_mix, 0);
}

vec4 ntsc(vec2 fragCoord)
{
    float Fs = iResolution.x;
    float Fcol = Fs * ntscFreqColor;
    float Flumlp = Fs * ntscFreqLuma;
    float n = floor(fragCoord.x);

	vec2 uv = fragCoord.xy;

    float luma = getLumaChromaSignal(uv).r;
    vec2 chroma = vec2(0);

    //Filtering out unwanted high freqency content from the chroma(IQ) signal.
    for(int i = 0;i < impulseResponseSize;i++)
    {
        int tpidx = impulseResponseSize - i - 1;
        float lp = Lowpass(Flumlp, Fs, impulseResponseSize, tpidx);
        chroma += getLumaChromaSignal(uv - vec2(i - impulseResponseSize * 0.5, 0)).yz * lp;
    }

    vec3 color = yiq2rgb * vec3(luma, chroma * saturation);

    return vec4(color, 1.0);
}

void main()
{
    vec2 TexCoords = gl_FragCoord.xy / iResolution.xy;

	if (!enabled)
	{
		FragColor = texture(iChannel0, TexCoords);
		return;
	}

    // current time seed
    vec2 now = vec2(0., time * 10.);

    // create UV
    vec2 uv = TexCoords;

    // jitter lines
    uv = ntscInterlaceJitter(now, uv);

    // sample texture
    vec4 col = ntsc(uv * iResolution.xy);

    // add grain
    col += vec4(vec3(grainAmplitude * (rand(gl_FragCoord.xy + now) * 2. - 1.)), 0.0);

    // output to screen
    FragColor = col;
}
