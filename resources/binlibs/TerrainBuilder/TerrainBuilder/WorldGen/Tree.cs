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

            if (pos.Y < 1 || pos.Y + i + 1 > 256) return;

            const int k2 = 3;
            const int l2 = 0;

            for (var y = pos.Y - k2 + i; y <= pos.Y + i; ++y)
            {
                var nY = y - (pos.Y + i);
                var expansionSize = l2 + 1 - nY / 2;

                for (var x = pos.X - expansionSize; x <= pos.X + expansionSize; ++x)
                {
                    var nX = x - pos.X;

                    for (var z = pos.Z - expansionSize; z <= pos.Z + expansionSize; ++z)
                    {
                        var nZ = z - pos.Z;

                        if (Math.Abs(Math.Abs(nX) - expansionSize) < 0.1f && Math.Abs(Math.Abs(nZ) - expansionSize) < 0.1f && (Rand.Next(2) == 0 || Math.Abs(nY) < 0.1f))
                            continue;
                            
                        SetBlock(vbi, new Vector3((int)x, (int)y, (int)z), ColorLeaves);
                    }
                }
            }

            for (var j3 = 0; j3 < i; ++j3)
            {
                SetBlock(vbi, Intify(pos + Vector3.UnitY * j3), ColorWood);
            }
        }

        private static Vector3 Intify(Vector3 v)
        {
            return new Vector3((int)v.X, (int)v.Y, (int)v.Z);
        }
    }
}
