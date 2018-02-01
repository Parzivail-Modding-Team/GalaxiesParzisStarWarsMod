using System;
using OpenTK;
using PFX;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    abstract class TreeDecorator
    {
        // Note: colors are in BGR format
        protected static readonly int ColorLeaves = 0x4cbd18;
        protected static readonly int ColorWood = 0x2f4a58;
        protected static readonly Random Rand = new Random();

        protected static readonly DefaultTree DefaultTree = new DefaultTree(6);

        public static void BuildTree(VertexBufferInitializer vbi, Vector3 pos, int type)
        {
            switch (type)
            {
                case 1:
                    DefaultTree.Generate(vbi, pos);
                    break;
                default:
                    Lumberjack.Warn($"Unimplemented tree type: {type}");
                    break;
            }
        }

        protected abstract void Generate(VertexBufferInitializer vbi, Vector3 pos);

        protected void SetBlock(VertexBufferInitializer vbi, Vector3 pos, int color)
        {
            WindowVisualize.DrawBox(vbi, pos, color);
        }
    }
}
