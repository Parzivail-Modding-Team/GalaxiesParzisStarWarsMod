using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrainBuilder
{
    class Util
    {
        public static int RgbToInt(float r, float g, float b)
        {
            return ((int)(r * 255) << 0) | ((int)(g * 255) << 8) | ((int)(b * 255) << 16);
        }

        public static double Min(params double[] values)
        {
            return values.Min();
        }
    }
}
