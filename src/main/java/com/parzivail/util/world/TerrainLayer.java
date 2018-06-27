package com.parzivail.util.world;

import com.parzivail.util.common.OpenSimplexNoise;

/**
 * Created by colby on 12/27/2016.
 */
public class TerrainLayer
{
	private final OpenSimplexNoise noise;
	public Function function = Function.Simplex;
	public Method method = Method.Add;
	public double scale = 200;
	public double range = 20;

	public TerrainLayer(long seed, Function function, Method method, double scale, double range)
	{
		this.function = function;
		this.method = method;
		this.scale = scale;
		this.range = range;
		noise = new OpenSimplexNoise(seed);
	}

	public TerrainLayer(long seed, Method method, double scale, double range)
	{
		this(seed, Function.Simplex, method, scale, range);
	}

	public double GetValue(double x, double y)
	{
		double raw = noise.eval(x / scale, y / scale);

		switch (function)
		{
			case Simplex:
				double simplex = raw + 0.5;

				if (simplex < 0)
					simplex = 0;
				if (simplex > 1)
					simplex = 1;

				simplex *= range;
				return simplex;
			case Turbulent:
				double turb = -0.5;
				turb += Math.abs(raw) * 2;

				if (turb < 0)
					turb = 0;
				if (turb > 1)
					turb = 1;

				turb *= range;
				return turb;
			case InvTurbulent:
				double iturb = -0.5;
				iturb += Math.abs(raw) * 2;

				if (iturb < 0)
					iturb = 0;
				if (iturb > 1)
					iturb = 1;

				iturb = 1 - iturb;

				iturb *= range;
				return iturb;
			case NCTurbulent:
				double ncturb = Math.abs(raw);

				if (ncturb < 0)
					ncturb = 0;
				if (ncturb > 1)
					ncturb = 1;

				ncturb *= range;
				return ncturb;
			case InvNCTurbulent:
				double incturb = Math.abs(raw);

				if (incturb < 0)
					incturb = 0;
				if (incturb > 1)
					incturb = 1;

				incturb = 1 - incturb;

				incturb *= range;
				return incturb;
			case Midpoint:
				double midpt = -Math.abs(2 * raw - 1) + 1;

				if (midpt < 0)
					midpt = 0;
				if (midpt > 1)
					midpt = 1;

				midpt *= range;
				return midpt;
			case InvMidpoint:
				double imidpt = -Math.abs(2 * raw - 1) + 1;

				if (imidpt < 0)
					imidpt = 0;
				if (imidpt > 1)
					imidpt = 1;

				imidpt = 1 - imidpt;

				imidpt *= range;
				return imidpt;
			case FilmMelt:
				double t = raw;
				double a = Math.pow(Math.E, 54 * (t - 0.81)) + 1;
				double b = Math.pow(Math.E, 10 * (-t + 0.19)) + 1;
				double c = Math.pow(45, Math.pow(-(5 * t - 2.5), 2));
				double filmmelt = (1 / (a * b) - c / 20) / 0.9969;

				if (filmmelt < 0)
					filmmelt = 0;
				if (filmmelt > 1)
					filmmelt = 1;

				filmmelt *= range;
				return filmmelt;
			case Warble:
				raw = (raw + 1) / 2;
				double warble = Math.abs(raw * Math.sin(100 * raw));

				if (warble < 0)
					warble = 0;
				if (warble > 1)
					warble = 1;

				warble *= range;
				return warble;
			case InvWarble:
				raw = (raw + 1) / 2;
				double iwarble = Math.abs(raw * Math.sin(100 * raw));

				if (iwarble < 0)
					iwarble = 0;
				if (iwarble > 1)
					iwarble = 1;

				iwarble = 1 - iwarble;

				iwarble *= range;
				return iwarble;
			case Klump:
				raw = (raw + 1) / 2;
				double klump = raw * 1.7265 * Math.sin(Math.PI * raw);

				if (klump < 0)
					klump = 0;
				if (klump > 1)
					klump = 1;

				klump *= range;
				return klump;
			case InvKlump:
				raw = (raw + 1) / 2;
				double iklump = raw * 1.7265 * Math.sin(Math.PI * raw);

				if (iklump < 0)
					iklump = 0;
				if (iklump > 1)
					iklump = 1;

				iklump = 1 - iklump;

				iklump *= range;
				return iklump;
			case HiLoPass:
				raw = (raw + 1) / 2;
				double hlp = Math.pow(raw, Math.sin(Math.PI * raw));

				if (hlp < 0)
					hlp = 0;
				if (hlp > 1)
					hlp = 1;

				hlp *= range;
				return hlp;
			case InvHiLoPass:
				raw = (raw + 1) / 2;
				double ihlp = Math.pow(raw, Math.sin(Math.PI * raw));

				if (ihlp < 0)
					ihlp = 0;
				if (ihlp > 1)
					ihlp = 1;

				ihlp = 1 - ihlp;

				ihlp *= range;
				return ihlp;
			case MidWave:
				raw = (raw + 1) / 2;
				double midwave = raw + (Math.sin(raw * Math.PI * 2) + 1) / 2 - 0.5;

				if (midwave < 0)
					midwave = 0;
				if (midwave > 1)
					midwave = 1;

				midwave *= range;
				return midwave;
			case Constant:
				return range;
			default:
				return 0;
		}
	}

	public enum Method
	{
		Add, Multiply, Subtract
	}

	public enum Function
	{
		Simplex, Turbulent, InvTurbulent, NCTurbulent, InvNCTurbulent, Midpoint, InvMidpoint, FilmMelt, Warble, InvWarble, Klump, InvKlump, HiLoPass, InvHiLoPass, MidWave, Constant
	}
}