using System;
using OpenTK;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    internal class RedwoodTree : TreeDecorator
    {
        private readonly int _minTreeHeight;
        private readonly int _heightChange;

        public RedwoodTree(int minTreeHeight, int heightChange)
        {
            _minTreeHeight = minTreeHeight;
            _heightChange = heightChange;
        }

        protected override void Generate(VertexBufferInitializer vbi, Vector3 pos)
        {
            var l = Rand.Next(_heightChange) + _minTreeHeight;

            if (pos.Y < 1 || pos.Y + l + 1 > 256) return;

            const int b0 = 9;
            const byte b1 = 0;

            for (var k1 = pos.Y - b0 + l; k1 <= pos.Y + l; ++k1)
            {
                var i3 = k1 - (pos.Y + l);
                var l1 = b1 + 1 - i3 / 4;

                for (var i2 = pos.X - l1; i2 <= pos.X + l1; ++i2)
                {
                    var j2 = i2 - pos.X;

                    for (var k2 = pos.Z - l1; k2 <= pos.Z + l1; ++k2)
                    {
                        var l2 = k2 - pos.Z;

                        if (!(Math.Abs(Math.Abs(j2) - l1) > 0.1f) && !(Math.Abs(Math.Abs(l2) - l1) > 0.1f) &&
                            (Rand.Next(2) == 0 || !(Math.Abs(i3) > 0.1f))) continue;

                        SetBlock(vbi, new Vector3(i2, k1 + 6, k2), ColorLeaves);
                        SetBlock(vbi, new Vector3(i2, k1 + 10, k2), ColorLeaves);
                    }
                }
            }

            for (var k1 = 0; k1 < l; ++k1)
            {
                //1
                SetBlock(vbi, new Vector3(pos.X, pos.Y + l, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + (l + 1), pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + (l + 2), pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + (l + 3), pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + l, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + l, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, pos.Y + k1, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, pos.Y + k1, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1, pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1, pos.Z + 1), ColorWood);

                //2
                SetBlock(vbi, new Vector3(pos.X - 1, (float) (pos.Y + Math.Floor(k1 * 0.75)),pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, (float) (pos.Y + Math.Floor(k1 * 0.75)),pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, (float) (pos.Y + Math.Floor(k1 * 0.75)),pos.Z + 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, (float) (pos.Y + Math.Floor(k1 * 0.75)),pos.Z + 1), ColorWood);

                //3
                SetBlock(vbi, new Vector3(pos.X - 2, pos.Y + k1 / 2, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 2, pos.Y + k1 / 2, pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 2, pos.Y + k1 / 2, pos.Z + 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 2, pos.Y + k1 / 2, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 2, pos.Y + k1 / 2, pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 2, pos.Y + k1 / 2, pos.Z + 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1 / 2, pos.Z - 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, pos.Y + k1 / 2, pos.Z - 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, pos.Y + k1 / 2, pos.Z - 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1 / 2, pos.Z + 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, pos.Y + k1 / 2, pos.Z + 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, pos.Y + k1 / 2, pos.Z + 2), ColorWood);

                //4
                SetBlock(vbi, new Vector3(pos.X - 2, pos.Y + k1 / 4, pos.Z - 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 2, pos.Y + k1 / 4, pos.Z + 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 2, pos.Y + k1 / 4, pos.Z - 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 2, pos.Y + k1 / 4, pos.Z + 2), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 3, pos.Y + k1 / 4, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 3, pos.Y + k1 / 4, pos.Z), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1 / 4, pos.Z - 3), ColorWood);
                SetBlock(vbi, new Vector3(pos.X, pos.Y + k1 / 4, pos.Z + 3), ColorWood);

                //5
                SetBlock(vbi, new Vector3(pos.X - 3, pos.Y + k1 / 6, pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 3, pos.Y + k1 / 6, pos.Z + 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 3, pos.Y + k1 / 6, pos.Z - 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 3, pos.Y + k1 / 6, pos.Z + 1), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, pos.Y + k1 / 6, pos.Z - 3), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, pos.Y + k1 / 6, pos.Z - 3), ColorWood);
                SetBlock(vbi, new Vector3(pos.X - 1, pos.Y + k1 / 6, pos.Z + 3), ColorWood);
                SetBlock(vbi, new Vector3(pos.X + 1, pos.Y + k1 / 6, pos.Z + 3), ColorWood);
            }
        }
    }
}
