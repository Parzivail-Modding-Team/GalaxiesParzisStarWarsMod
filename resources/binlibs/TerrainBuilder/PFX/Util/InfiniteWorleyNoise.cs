using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PFX.Util
{
    public class InfiniteWorleyNoise
    {
        private readonly long _seed;

        public InfiniteWorleyNoise(long seed)
        {
            _seed = seed;
        }

        public InfiniteWorleyNoise() : this(0)
        {
        }

        public double Eval(double nx, double ny)
        {
            double dis = 2;

            for (var x = -1; x <= 1; x++)
            for (var y = -1; y <= 1; y++)
            {
                var px = Math.Floor(nx) + x;
                var py = Math.Floor(ny) + y;
                    
                var rx = Math.Abs(Math.Cos((px * 23.62 - 300.0 + py * 34.35) * 89.42) * 343.42) % 1;
                var ry = Math.Abs(Math.Cos((px * 45.13 + 256.0 + py * 38.89) * 89.42) * 343.42) % 1;
                    
                var d = Math.Sqrt(Math.Pow(rx + x - nx % 1, 2) + Math.Pow(ry + y - ny % 1, 2));
                    
                dis = Math.Min(dis, d);
            }

            return dis;
        }

        private double Seed(double d)
        {
            return d;//BitConverter.Int64BitsToDouble(BitConverter.DoubleToInt64Bits(d) ^ _seed);
        }
    }
}
