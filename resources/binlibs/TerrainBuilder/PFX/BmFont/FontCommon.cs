namespace PFX.BmFont
{
    public class FontCommon
    {
        public static uint BlockType { get; set; }
        public static uint BlockSize { get; set; }
        public short LineHeight { get; set; }
        public short Base { get; set; }
        public short ScaleW { get; set; }
        public short ScaleH { get; set; }
        public short Pages { get; set; }
        public uint Packed { get; set; }
        public uint AlphaChannel { get; set; }
        public uint RedChannel { get; set; }
        public uint GreenChannel { get; set; }
        public uint BlueChannel { get; set; }
    }
}