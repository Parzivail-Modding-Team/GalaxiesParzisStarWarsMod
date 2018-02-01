using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenTK;
using PFX;
using PFX.Util;

namespace TerrainBuilder
{
    class TreeDecorator
    {
        private static readonly int ColorLeaves = 0x0000FF;
        private static readonly int ColorWood = 0xFF0000;

        public static void BuildTree(VertexBufferInitializer vbi, Vector3 pos, int type)
        {
            switch (type)
            {
                default:
                    Lumberjack.Warn($"Unimplemented tree type: {type}");
                    break;
            }
        }

        private void SetBlock(VertexBufferInitializer vbi, Vector3 pos, int color)
        {
            WindowVisualize.DrawBox(vbi, pos, color);
        }
    }
}
