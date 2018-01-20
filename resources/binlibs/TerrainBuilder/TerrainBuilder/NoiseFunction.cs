using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrainBuilder
{
    public enum NoiseFunction
    {
        Simplex,
        Turbulent,
        InvTurbulent,
        NCTurbulent,
        InvNCTurbulent,
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
