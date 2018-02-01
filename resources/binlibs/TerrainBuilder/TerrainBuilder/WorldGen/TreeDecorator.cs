using System;
using OpenTK;
using PFX;
using PFX.Util;

namespace TerrainBuilder.WorldGen
{
    abstract class TreeDecorator
    {
        protected static readonly int ColorLeaves = 0x18bd4c;
        protected static readonly int ColorWood = 0x83652c;
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
