using System;
using OpenTK;
using PFX;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    abstract class TreeDecorator
    {
        protected static readonly int ColorLeaves = 0x0000AA;
        protected static readonly int ColorWood = 0xFF0000;
        protected static readonly Random Rand = new Random();

        protected static readonly Tree Tree = new Tree(6);

        public static void BuildTree(VertexBufferInitializer vbi, Vector3 pos, int type)
        {
            switch (type)
            {
                case 1:
                    Tree.Generate(vbi, pos);
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
