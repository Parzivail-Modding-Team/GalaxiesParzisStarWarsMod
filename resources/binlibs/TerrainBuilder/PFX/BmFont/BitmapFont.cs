using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using OpenTK.Graphics.OpenGL;

namespace PFX.BmFont
{
    public class BitmapFont
    {
        private readonly Dictionary<string, int> _cacheTextList = new Dictionary<string, int>();

        private readonly Dictionary<string, SizeF> _cacheTextSize = new Dictionary<string, SizeF>();
        public string Filename { get; set; }
        public FontInfo Info { get; set; }
        public FontCommon Common { get; set; }
        public FontPages Pages { get; set; }
        public List<FontChar> Characters { get; set; }
        public List<FontKerning> KerningPairs { get; set; }
        public FontChar MissingCharacter { get; set; }

        public void RenderString(string s, bool cache = true)
        {
            if (_cacheTextList.ContainsKey(s) && cache)
            {
                GL.PushMatrix();
                GL.CallList(_cacheTextList[s]);
                GL.PopMatrix();
                return;
            }

            GL.PushMatrix();
            if (cache)
            {
                var list = GL.GenLists(1);
                _cacheTextList.Add(s, list);
                GL.NewList(list, ListMode.CompileAndExecute);
            }

            var chars = s.ToCharArray();

            var cursor = new PointF(0, 0);
            foreach (var c in chars)
            {
                if (c == '\n')
                {
                    cursor.X = 0;
                    cursor.Y += Common.LineHeight;
                    continue;
                }

                var fontChar = MissingCharacter;
                if (Characters.Any(fc => fc.Id == c))
                    fontChar = Characters.First(fc => fc.Id == c);

                GL.PushMatrix();
                GL.Translate(cursor.X + fontChar.OffsetX, cursor.Y + fontChar.OffsetY, 0);
                GL.BindTexture(TextureTarget.Texture2D, fontChar.CharacterBitmap.Key);
                GL.Begin(PrimitiveType.Quads);
                GL.TexCoord2(0, 0);
                GL.Vertex2(0, 0);
                GL.TexCoord2(1, 0);
                GL.Vertex2(fontChar.Width, 0);
                GL.TexCoord2(1, 1);
                GL.Vertex2(fontChar.Width, fontChar.Height);
                GL.TexCoord2(0, 1);
                GL.Vertex2(0, fontChar.Height);
                GL.End();
                GL.PopMatrix();

                cursor.X += fontChar.AdvanceX;
            }

            if (cache)
                GL.EndList();
            GL.PopMatrix();
        }

        public SizeF MeasureString(string s)
        {
            if (_cacheTextSize.ContainsKey(s))
                return _cacheTextSize[s];

            var chars = s.ToCharArray();

            var size = new SizeF(0, 0);
            var cursor = new PointF(0, 0);
            foreach (var c in chars)
            {
                if (c == '\n')
                {
                    cursor.X = 0;
                    cursor.Y += Common.LineHeight;
                    continue;
                }

                var fontChar = Characters.Any(fC => fC.Id == c) ? Characters.First(fC => fC.Id == c) : MissingCharacter;

                var nx = cursor.X + fontChar.OffsetX + (c == ' ' ? fontChar.AdvanceX : fontChar.Width);
                var ny = cursor.Y + fontChar.OffsetY + fontChar.Height;

                cursor.X += fontChar.AdvanceX;

                if (nx > size.Width)
                    size.Width = nx;
                if (ny > size.Height)
                    size.Height = ny;
            }

            _cacheTextSize.Add(s, size);

            return size;
        }

        private void LoadPageBitmaps()
        {
            var path = Path.GetDirectoryName(Filename);

            Pages.PageBitmaps = new List<Bitmap>();
            foreach (var pageName in Pages.PageNames)
            {
                if (!File.Exists(path + $"\\{pageName}"))
                    throw new FileNotFoundException($"Could not load font page \"{path}\\{pageName}\"");

                Pages.PageBitmaps.Add((Bitmap) Image.FromFile(path + $"\\{pageName}"));
            }
        }

        private void LoadCharacterBitmapData()
        {
            foreach (var c in Characters)
            {
                var b = new Bitmap(c.Width, c.Height);
                var g = Graphics.FromImage(b);
                g.DrawImage(Pages.PageBitmaps[c.Page],
                    new Rectangle(0, 0, c.Width, c.Height),
                    c.X, c.Y, c.Width, c.Height,
                    GraphicsUnit.Pixel);

                c.CharacterBitmap = b.LoadGlTexture();
            }
        }

        public static BitmapFont LoadBinaryFont(string filename)
        {
            if (!File.Exists(filename))
                throw new FileNotFoundException($"Could not load font file `{filename}`");

            return LoadBinaryFont(filename, File.ReadAllBytes(filename));
        }

        public static BitmapFont LoadBinaryFont(string filename, byte[] binaryFntFile, params Bitmap[] pages)
        {
            var font = new BitmapFont
            {
                Filename = filename,
                Info = new FontInfo(),
                Common = new FontCommon(),
                Pages = new FontPages()
            };

            using (var sr = new MemoryStream(binaryFntFile))
            using (var r = new BinaryReader(sr))
            {
                var header = r.ReadBytes(3);
                if (header.GetString() != "BMF")
                    throw new FileLoadException(
                        $"The font \"{filename}\" does not have a valid header. Expected \"BMF\" (0x66, 0x77, 0x70), found {header.GetString() + "(" + string.Join(", ", header.Select(b => "0x" + b.ToString("X"))) + ")"}");

                var version = r.ReadByte();
                if (version != 3)
                    throw new FileLoadException(
                        $"The font \"{filename}\" does not have a valid header. Expected version 3, found version {version}");

                FontInfo.BlockType = r.ReadByte();
                FontInfo.BlockSize = r.ReadUInt32();

                font.Info.Size = r.ReadInt16();
                font.Info.BitField = r.ReadByte();
                font.Info.CharSet = r.ReadByte();
                font.Info.StretchH = r.ReadInt16();
                font.Info.SupersampleLevel = r.ReadByte();
                font.Info.PaddingUp = r.ReadByte();
                font.Info.PaddingRight = r.ReadByte();
                font.Info.PaddingDown = r.ReadByte();
                font.Info.PaddingLeft = r.ReadByte();
                font.Info.SpacingHoriz = r.ReadByte();
                font.Info.SpacingVert = r.ReadByte();
                font.Info.OutlineWidth = r.ReadByte();
                font.Info.FontName = r.ReadNullTermString();

                FontCommon.BlockType = r.ReadByte();
                FontCommon.BlockSize = r.ReadUInt32();

                font.Common.LineHeight = r.ReadInt16();
                font.Common.Base = r.ReadInt16();
                font.Common.ScaleW = r.ReadInt16();
                font.Common.ScaleH = r.ReadInt16();
                font.Common.Pages = r.ReadInt16();
                font.Common.Packed = r.ReadByte();
                font.Common.AlphaChannel = r.ReadByte();
                font.Common.RedChannel = r.ReadByte();
                font.Common.GreenChannel = r.ReadByte();
                font.Common.BlueChannel = r.ReadByte();

                FontPages.BlockType = r.ReadByte();
                FontPages.BlockSize = r.ReadUInt32();

                font.Pages.PageNames = new List<string>();
                for (var i = 0; i < font.Common.Pages; i++)
                    font.Pages.PageNames.Add(r.ReadNullTermString());

                FontChar.BlockType = r.ReadByte();
                FontChar.BlockSize = r.ReadUInt32();

                var numChars = FontChar.BlockSize / 20; // 20 bytes per char struct

                font.Characters = new List<FontChar>();
                for (var i = 0; i < numChars; i++)
                    font.Characters.Add(new FontChar
                    {
                        Id = r.ReadUInt32(),
                        X = r.ReadUInt16(),
                        Y = r.ReadUInt16(),
                        Width = r.ReadUInt16(),
                        Height = r.ReadUInt16(),
                        OffsetX = r.ReadInt16(),
                        OffsetY = r.ReadInt16(),
                        AdvanceX = r.ReadInt16(),
                        Page = r.ReadByte(),
                        Channel = r.ReadByte()
                    });

                font.MissingCharacter = font.Characters.First(fC => fC.Id == '?');

                if (pages.Length == 0)
                {
                    font.LoadPageBitmaps();
                }
                else
                {
                    font.Pages.PageBitmaps = new List<Bitmap>();
                    font.Pages.PageBitmaps.AddRange(pages);
                    if (font.Pages.PageBitmaps.Count != font.Pages.PageNames.Count)
                        throw new FileLoadException(
                            $"Number of page bitmaps provided ({pages.Length}) did not match expected ({font.Pages.PageBitmaps.Count}).");
                }

                font.LoadCharacterBitmapData();

                if (sr.Position == sr.Length) return font;

                FontKerning.BlockType = r.ReadByte();
                FontKerning.BlockSize = r.ReadUInt32();

                var numPairs = FontKerning.BlockSize / 10; // 10 bytes per char struct

                for (var i = 0; i < numPairs; i++)
                    font.KerningPairs.Add(new FontKerning
                    {
                        First = r.ReadUInt32(),
                        Second = r.ReadUInt32(),
                        Amount = r.ReadInt16()
                    });
            }

            return font;
        }
    }
}