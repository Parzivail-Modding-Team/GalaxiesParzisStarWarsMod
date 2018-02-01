using System;
using OpenTK;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    internal class Tree : TreeDecorator
    {
        private readonly int _minTreeHeight;

        public Tree(int minTreeHeight)
        {
            _minTreeHeight = minTreeHeight;
        }

        protected override void Generate(VertexBufferInitializer vbi, Vector3 pos)
        {
            var i = Rand.Next(3) + _minTreeHeight;
            var flag = true;

            if ((int) pos.Y < 1 || (int) pos.Y + i + 1 > 256) return;

            for (var j = (int)pos.Y; j <= (int)pos.Y + 1 + i; ++j)
            {
                var k = 1;

                if (j == (int)pos.Y)
                {
                    k = 0;
                }

                if (j >= (int)pos.Y + 1 + i - 2)
                {
                    k = 2;
                }

                for (var l = (int)pos.X - k; l <= (int)pos.X + k && flag; ++l)
                {
                    for (var i1 = (int)pos.Z - k; i1 <= (int)pos.Z + k && flag; ++i1)
                    {
                        if (j < 0 || j >= 256)
                            flag = false;
                    }
                }
            }

            if (!flag) return;

            const int k2 = 3;
            const int l2 = 0;

            for (var i3 = (int) pos.Y - k2 + i; i3 <= (int) pos.Y + i; ++i3)
            {
                var i4 = i3 - ((int) pos.Y + i);
                var j1 = l2 + 1 - i4 / 2;

                for (var k1 = (int) pos.X - j1; k1 <= (int) pos.X + j1; ++k1)
                {
                    var l1 = k1 - (int) pos.X;

                    for (var i2 = (int) pos.Z - j1; i2 <= (int) pos.Z + j1; ++i2)
                    {
                        var j2 = i2 - (int) pos.Z;

                        if (Math.Abs(l1) == j1 && Math.Abs(j2) == j1 && (Rand.Next(2) == 0 || i4 == 0))
                            continue;
                            
                        SetBlock(vbi, new Vector3(k1, i3, i2), ColorLeaves);
                    }
                }
            }

            for (var j3 = 0; j3 < i; ++j3)
            {
                SetBlock(vbi, pos + Vector3.UnitY * j3, ColorWood);
            }
        }
    }
}
