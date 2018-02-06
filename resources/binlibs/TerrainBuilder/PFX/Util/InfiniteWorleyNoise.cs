using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using OpenTK;

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
            return Worley(new Vector2d(nx, ny));
        }

        public double Eval(double nx, double ny, double nz)
        {
            return Worley(new Vector3d(nx, ny, nz));
        }

        private double R(double n)
        {
            return Fract(Math.Cos(Seed(n * 89.42)) * 343.42);
        }

        private Vector2d R(Vector2d n)
        {
            return new Vector2d(R(n.X * 23.62f - 300 + n.Y * 34.35f), R(n.X * 45.13f + 256 + n.Y * 38.89f));
        }

        private Vector3d R(Vector3d n)
        {
            return new Vector3d(R(n.X * 23.62f - 300 + n.Y * 34.35f + 663 + n.Z * 36.57f), R(n.X * 45.13f + 256 + n.Y * 38.89f - 764 + n.Z * 91.58f), R(n.X * 13.2f - 458 + n.Y * 56.24f + 172 + n.Z * 68.45f));
        }

        private static double Fract(double d)
        {
            return d - Math.Floor(d);
        }

        private static Vector2d Fract(Vector2d d)
        {
            d.X = Fract(d.X);
            d.Y = Fract(d.Y);
            return d;
        }

        private static Vector3d Fract(Vector3d d)
        {
            d.X = Fract(d.X);
            d.Y = Fract(d.Y);
            d.Z = Fract(d.Z);
            return d;
        }

        private static Vector2d Floor(Vector2d vector2)
        {
            vector2.X = (float) Math.Floor(vector2.X);
            vector2.Y = (float) Math.Floor(vector2.Y);
            return vector2;
        }

        private static Vector3d Floor(Vector3d vector2)
        {
            vector2.X = (float) Math.Floor(vector2.X);
            vector2.Y = (float) Math.Floor(vector2.Y);
            vector2.Z = (float) Math.Floor(vector2.Z);
            return vector2;
        }

        private double Worley(Vector2d n)
        {
            var dis = 2d;
            var id = 0;
            for (var x = -1; x <= 1; x++)
            {
                for (var y = -1; y <= 1; y++)
                {
                    var q = new Vector2d(x, y);
                    var p = Floor(n) + q;
                    var d = (R(p) + q - Fract(n)).Length;

                    if (!(dis > d)) continue;

                    dis = d;
                    //id = Math.Abs(R(p).ToString().GetHashCode()) % 10;
                }
            }
            return dis;
        }

        private double Worley(Vector3d n)
        {
            var dis = 2d;
            var id = 0;
            for (var x = -1; x <= 1; x++)
            {
                for (var y = -1; y <= 1; y++)
                {
                    for (var z = -1; z <= 1; z++)
                    {
                        var q = new Vector3d(x, y, z);
                        var p = Floor(n) + q;
                        var d = (R(p) + q - Fract(n)).Length;

                        if (!(dis > d)) continue;

                        dis = d;
                        //id = Math.Abs(R(p).ToString().GetHashCode()) % 10;
                    }
                }
            }
            return dis;
        }

        private double Seed(double d)
        {
            return BitConverter.Int64BitsToDouble(BitConverter.DoubleToInt64Bits(d) ^ _seed);
        }
    }
}
