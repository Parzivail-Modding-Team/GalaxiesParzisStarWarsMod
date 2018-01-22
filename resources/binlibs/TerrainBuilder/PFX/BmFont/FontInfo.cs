namespace PFX.BmFont
{
    public class FontInfo
    {
        public static uint BlockType { get; set; }
        public static uint BlockSize { get; set; }
        public short Size { get; set; }
        public uint BitField { get; set; }
        public uint CharSet { get; set; }
        public short StretchH { get; set; }
        public uint SupersampleLevel { get; set; }
        public uint PaddingUp { get; set; }
        public uint PaddingRight { get; set; }
        public uint PaddingDown { get; set; }
        public uint PaddingLeft { get; set; }
        public uint SpacingHoriz { get; set; }
        public uint SpacingVert { get; set; }
        public uint OutlineWidth { get; set; }
        public string FontName { get; set; }
    }
}