using System.Collections.Generic;
using System.Drawing;

namespace PFX.BmFont
{
    public class FontPages
    {
        public static uint BlockType { get; set; }
        public static uint BlockSize { get; set; }
        public List<string> PageNames { get; set; }
        public List<Bitmap> PageBitmaps { get; set; }
    }
}