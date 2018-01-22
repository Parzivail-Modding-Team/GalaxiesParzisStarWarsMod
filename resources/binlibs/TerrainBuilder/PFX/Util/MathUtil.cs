namespace PFX.Util
{
    internal class MathUtil
    {
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
    }
}