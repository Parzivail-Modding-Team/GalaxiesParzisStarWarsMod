using System.Collections.Generic;
using System.Drawing;

namespace PFX.BmFont
{
    public class FontChar
    {
        public static uint BlockType { get; set; }
        public static uint BlockSize { get; set; }
        public KeyValuePair<int, Bitmap> CharacterBitmap { get; set; }
        public uint Id { get; set; }
        public uint X { get; set; }
        public uint Y { get; set; }
        public int Width { get; set; }
        public int Height { get; set; }
        public int OffsetX { get; set; }
        public int OffsetY { get; set; }
        public int AdvanceX { get; set; }
        public int Page { get; set; }
        public uint Channel { get; set; }
    }
}