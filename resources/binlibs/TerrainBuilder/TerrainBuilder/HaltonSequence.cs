using OpenTK;

namespace TerrainBuilder
{
    public class HaltonSequence
    {
        private Vector3 _currentPos = new Vector3(0.0f, 0.0f, 0.0f);
        private long _mBase2;
        private long _mBase3;
        private long _mBase5;

        public Vector3 Increment()
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
                    _currentPos.X -= s;
                else
                    _currentPos.X += s;

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
                    _currentPos.Y -= 2 * s;

                    bitmask = bitmask << 2;
                    bitadd = bitadd << 2;

                    s *= fOneOver3;
                }
                else
                {
                    _currentPos.Y += s;
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
                    _currentPos.Z -= 4 * s;

                    bitmask = bitmask << 3;
                    dmax = dmax << 3;
                    bitadd = bitadd << 3;

                    s *= fOneOver5;
                }
                else
                {
                    _currentPos.Z += s;
                    break;
                }
            }

            return _currentPos;
        }

        public void Reset()
        {
            _currentPos.X = 0.0f;
            _currentPos.Y = 0.0f;
            _currentPos.Z = 0.0f;
            _mBase2 = 0;
            _mBase3 = 0;
            _mBase5 = 0;
        }
    }
}
