using System;
using OpenTK;

namespace PFX.Util
{
    public class MathUtil
    {
        public const double OneOverGoldenRatio = 0.61803398875;

        public static float EaseOut(float t, float b, float c, float d)
        {
            t /= d;
            return c * t * t * t * t * t + b;
        }

        public static float EaseIn(float t, float b, float c, float d)
        {
            t /= d;
            t--;
            return c * (t * t * t * t * t + 1) + b;
        }

        public static double Fract(double d)
        {
            return d - Math.Floor(d);
        }

        public static Vector2d Fract(Vector2d d)
        {
            d.X = MathUtil.Fract(d.X);
            d.Y = MathUtil.Fract(d.Y);
            return d;
        }

        public static Vector3d Fract(Vector3d d)
        {
            d.X = MathUtil.Fract(d.X);
            d.Y = MathUtil.Fract(d.Y);
            d.Z = MathUtil.Fract(d.Z);
            return d;
        }

        public static Vector2d Floor(Vector2d vector2)
        {
            vector2.X = (float) Math.Floor(vector2.X);
            vector2.Y = (float) Math.Floor(vector2.Y);
            return vector2;
        }

        public static Vector3d Floor(Vector3d vector2)
        {
            vector2.X = (float) Math.Floor(vector2.X);
            vector2.Y = (float) Math.Floor(vector2.Y);
            vector2.Z = (float) Math.Floor(vector2.Z);
            return vector2;
        }

        public static double Seed(double d, long seed)
        {
            return BitConverter.Int64BitsToDouble(BitConverter.DoubleToInt64Bits(d) ^ seed);
        }
    }
}