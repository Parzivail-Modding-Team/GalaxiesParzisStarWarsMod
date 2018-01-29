using System;

namespace TerrainBuilder
{
    [Obsolete("Left for implementation reference only", true)]
    public class TerrainLayer
    {
        [Obsolete("Left for implementation reference only", true)]
        public double GetValue(double x, double y)
        {
            var function = NoiseFunction.None;

            var raw = 0;
            const int range = 0;

            switch (function)
            {
                case NoiseFunction.Simplex:
                    var simplex = raw + 0.5;

                    if (simplex < 0)
                        simplex = 0;
                    if (simplex > 1)
                        simplex = 1;

                    simplex *= range;
                    return simplex;
                case NoiseFunction.Turbulent:
                    var turb = -0.5;
                    turb += Math.Abs(raw) * 2;

                    if (turb < 0)
                        turb = 0;
                    if (turb > 1)
                        turb = 1;

                    turb *= range;
                    return turb;
                case NoiseFunction.InvTurbulent:
                    var iturb = -0.5;
                    iturb += Math.Abs(raw) * 2;

                    if (iturb < 0)
                        iturb = 0;
                    if (iturb > 1)
                        iturb = 1;

                    iturb = 1 - iturb;

                    iturb *= range;
                    return iturb;
                case NoiseFunction.NcTurbulent:
                    var ncturb = Math.Abs(raw);

                    if (ncturb < 0)
                        ncturb = 0;
                    if (ncturb > 1)
                        ncturb = 1;

                    ncturb *= range;
                    return ncturb;
                case NoiseFunction.InvNcTurbulent:
                    var incturb = Math.Abs(raw);

                    if (incturb < 0)
                        incturb = 0;
                    if (incturb > 1)
                        incturb = 1;

                    incturb = 1 - incturb;

                    incturb *= range;
                    return incturb;
                case NoiseFunction.Midpoint:
                    var midpt = -Math.Abs(2 * raw - 1) + 1;

                    if (midpt < 0)
                        midpt = 0;
                    if (midpt > 1)
                        midpt = 1;

                    midpt *= range;
                    return midpt;
                case NoiseFunction.InvMidpoint:
                    var imidpt = -Math.Abs(2 * raw - 1) + 1;

                    if (imidpt < 0)
                        imidpt = 0;
                    if (imidpt > 1)
                        imidpt = 1;

                    imidpt = 1 - imidpt;

                    imidpt *= range;
                    return imidpt;
                case NoiseFunction.FilmMelt:
                    var t = raw;
                    var a = Math.Pow(Math.E, 54 * (t - 0.81)) + 1;
                    var b = Math.Pow(Math.E, 10 * (-t + 0.19)) + 1;
                    var c = Math.Pow(45, Math.Pow(-(5 * t - 2.5), 2));
                    var filmmelt = (1 / (a * b) - c / 20) / 0.9969;

                    if (filmmelt < 0)
                        filmmelt = 0;
                    if (filmmelt > 1)
                        filmmelt = 1;

                    filmmelt *= range;
                    return filmmelt;
                case NoiseFunction.Warble:
                    raw = (raw + 1) / 2;
                    var warble = Math.Abs(raw * Math.Sin(100 * raw));

                    if (warble < 0)
                        warble = 0;
                    if (warble > 1)
                        warble = 1;

                    warble *= range;
                    return warble;
                case NoiseFunction.InvWarble:
                    raw = (raw + 1) / 2;
                    var iwarble = Math.Abs(raw * Math.Sin(100 * raw));

                    if (iwarble < 0)
                        iwarble = 0;
                    if (iwarble > 1)
                        iwarble = 1;

                    iwarble = 1 - iwarble;

                    iwarble *= range;
                    return iwarble;
                case NoiseFunction.Klump:
                    raw = (raw + 1) / 2;
                    var klump = raw * 1.7265 * Math.Sin(Math.PI * raw);

                    if (klump < 0)
                        klump = 0;
                    if (klump > 1)
                        klump = 1;

                    klump *= range;
                    return klump;
                case NoiseFunction.InvKlump:
                    raw = (raw + 1) / 2;
                    var iklump = raw * 1.7265 * Math.Sin(Math.PI * raw);

                    if (iklump < 0)
                        iklump = 0;
                    if (iklump > 1)
                        iklump = 1;

                    iklump = 1 - iklump;

                    iklump *= range;
                    return iklump;
                case NoiseFunction.HiLoPass:
                    raw = (raw + 1) / 2;
                    var hlp = Math.Pow(raw, Math.Sin(Math.PI * raw));

                    if (hlp < 0)
                        hlp = 0;
                    if (hlp > 1)
                        hlp = 1;

                    hlp *= range;
                    return hlp;
                case NoiseFunction.InvHiLoPass:
                    raw = (raw + 1) / 2;
                    var ihlp = Math.Pow(raw, Math.Sin(Math.PI * raw));

                    if (ihlp < 0)
                        ihlp = 0;
                    if (ihlp > 1)
                        ihlp = 1;

                    ihlp = 1 - ihlp;

                    ihlp *= range;
                    return ihlp;
                case NoiseFunction.MidWave:
                    raw = (raw + 1) / 2;
                    var midwave = raw + (Math.Sin(raw * Math.PI * 2) + 1) / 2 - 0.5;

                    if (midwave < 0)
                        midwave = 0;
                    if (midwave > 1)
                        midwave = 1;

                    midwave *= range;
                    return midwave;
                case NoiseFunction.Constant:
                    return range;
                case NoiseFunction.None:
                    return 0;
                default:
                    throw new ArgumentOutOfRangeException();
            }
        }

        public enum NoiseFunction
        {
            None,
            Simplex,
            Turbulent,
            InvTurbulent,
            NcTurbulent,
            InvNcTurbulent,
            Midpoint,
            InvMidpoint,
            FilmMelt,
            Warble,
            InvWarble,
            Klump,
            InvKlump,
            HiLoPass,
            InvHiLoPass,
            MidWave,
            Constant
        }
    }
}