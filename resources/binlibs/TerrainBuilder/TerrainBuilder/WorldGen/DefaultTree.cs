using System;
using OpenTK;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    internal class DefaultTree : TreeDecorator
    {
        private readonly int _minTreeHeight;

        public DefaultTree(int minTreeHeight)
        {
            _minTreeHeight = minTreeHeight;
        }

        protected override void Generate(VertexBufferInitializer vbi, Vector3 pos)
        {
            var treeHeight = Rand.Next(3) + _minTreeHeight;

            if (pos.Y < 1 || pos.Y + treeHeight + 1 > 256) return;

            const int leavesHeight = 3;

            for (var y = pos.Y - leavesHeight + treeHeight; y <= pos.Y + treeHeight; ++y)
            {
                var nY = y - (pos.Y + treeHeight);
                var expansionSize = 1 - nY / 2;

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

            for (var j3 = 0; j3 < treeHeight; ++j3)
            {
                SetBlock(vbi, Intify(pos + Vector3.UnitY * j3), ColorWood);
            }
        }
    }
}
