using OpenTK;

namespace TerrainBuilder
{
    public class HaltonSequence
    {
        public Vector3 MCurrentPos = new Vector3(0.0f, 0.0f, 0.0f);
        long _mBase2;
        long _mBase3;
        long _mBase5;

        public long Increment()
        {
            const float fOneOver3 = 1.0f / 3.0f;
            const float fOneOver5 = 1.0f / 5.0f;

            var oldBase2 = _mBase2;
            _mBase2++;
            var diff = _mBase2 ^ oldBase2;

            var s = 0.5f;

            do
            {
                if ((oldBase2 & 1) == 1)
                    MCurrentPos.X -= s;
                else
                    MCurrentPos.X += s;

                s *= 0.5f;

                diff = diff >> 1;
                oldBase2 = oldBase2 >> 1;
            }
            while (diff > 0);

            long bitmask = 0x3;
            long bitadd = 0x1;
            s = fOneOver3;

            _mBase3++;

            while (true)
            {
                if ((_mBase3 & bitmask) == bitmask)
                {
                    _mBase3 += bitadd;
                    MCurrentPos.Y -= 2 * s;

                    bitmask = bitmask << 2;
                    bitadd = bitadd << 2;

                    s *= fOneOver3;
                }
                else
                {
                    MCurrentPos.Y += s;
                    break;
                }
            }
            bitmask = 0x7;
            bitadd = 0x3;
            long dmax = 0x5;

            s = fOneOver5;

            _mBase5++;

            while (true)
            {
                if ((_mBase5 & bitmask) == dmax)
                {
                    _mBase5 += bitadd;
                    MCurrentPos.Z -= 4 * s;

                    bitmask = bitmask << 3;
                    dmax = dmax << 3;
                    bitadd = bitadd << 3;

                    s *= fOneOver5;
                }
                else
                {
                    MCurrentPos.Z += s;
                    break;
                }
            }

            return _mBase2;
        }

        public void Reset()
        {
            MCurrentPos.X = 0.0f;
            MCurrentPos.Y = 0.0f;
            MCurrentPos.Z = 0.0f;
            _mBase2 = 0;
            _mBase3 = 0;
            _mBase5 = 0;
        }
    }
}
