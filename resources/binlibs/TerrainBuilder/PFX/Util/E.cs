using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using OpenTK.Graphics.OpenGL;
using PixelFormat = System.Drawing.Imaging.PixelFormat;

namespace PFX.Util
{
    public static class E
    {
        public static readonly Random Rand = new Random();

        /// <summary>
        ///     Color creator wrapper for hex colors
        /// </summary>
        /// <param name="hex">The color hex</param>
        /// <returns>A new color object</returns>
        public static Color ColorFromHex(string hex)
        {
            return ColorTranslator.FromHtml($"#{hex}");
        }

        /// <summary>
        ///     Picks a list object at (seeded) random
        /// </summary>
        /// <typeparam name="T">The type of haystack</typeparam>
        /// <param name="haystack">The list to pick from</param>
        /// <param name="random">The random float (0-1 inclusive) component</param>
        /// <returns>The list object at (random * listLength)</returns>
        public static T Random<T>(this IEnumerable<T> haystack, float random)
        {
            var l = haystack.ToList();
            return l[(int) (random * (l.Count - 1))];
        }

        /// <summary>
        ///     Uppercases the first letter in a string, for a sentence.
        /// </summary>
        /// <param name="s">The string to operate on</param>
        /// <returns>A string whose first letter is uppercase</returns>
        public static string UpperFirst(this string s)
        {
            if (s.Length < 2)
                return s.ToUpperInvariant();
            return s[0].ToString().ToUpperInvariant() + s.Substring(1);
        }

        /// <summary>
        ///     Maps a float from one range to another
        /// </summary>
        /// <param name="v">The value to map</param>
        /// <param name="minA">The original range minimum</param>
        /// <param name="maxA">The original range maximum</param>
        /// <param name="minB">The new range minimum</param>
        /// <param name="maxB">The new range maximum</param>
        /// <returns>The remapped float</returns>
        public static float Map(this float v, float minA, float maxA, float minB, float maxB)
        {
            return (v - minA) / (maxA - minA) * (maxB - minB) + minB;
        }

        /// <summary>
        ///     Maps a float from one range to another
        /// </summary>
        /// <param name="v">The value to map</param>
        /// <param name="minA">The original range minimum</param>
        /// <param name="maxA">The original range maximum</param>
        /// <param name="minB">The new range minimum</param>
        /// <param name="maxB">The new range maximum</param>
        /// <returns>The remapped int</returns>
        public static int Map(this int v, int minA, int maxA, int minB, int maxB)
        {
            return (int) Math.Round((v - minA) / (float) (maxA - minA) * (maxB - minB) + minB);
        }

        /// <summary>
        ///     Clamps a float between two values
        /// </summary>
        /// <param name="v">The value to clamp</param>
        /// <param name="min">The inclusive lower bound</param>
        /// <param name="max">The inclusive upper bound</param>
        /// <returns>The clamped float</returns>
        public static float Clamp(this float v, float min, float max)
        {
            return Math.Max(Math.Min(max, v), min);
        }

        /// <summary>
        ///     Rounds a float to the nearest [nearest]
        /// </summary>
        /// <param name="i">The float to round</param>
        /// <param name="nearest">The multiple to round to</param>
        /// <returns>The rounded float</returns>
        public static float RoundOff(this float i, float nearest)
        {
            return (float) Math.Round(i / nearest) * nearest;
        }

        /// <summary>
        ///     Splits a string into lines which are no longer than specified
        /// </summary>
        /// <param name="input">The string to split</param>
        /// <param name="maxCharInLine">The max number of chars in a line</param>
        /// <returns>A split string</returns>
        public static string SplitIntoLine(this string input, int maxCharInLine)
        {
            using (var tok = input.Split(' ').AsEnumerable().GetEnumerator())
            {
                var output = new StringBuilder(input.Length);
                var lineLen = 0;
                while (tok.MoveNext())
                {
                    var word = tok.Current;

                    while (word.Length > maxCharInLine)
                    {
                        output.Append(word.Substring(0, maxCharInLine - lineLen) + "\n");
                        word = word.Substring(maxCharInLine - lineLen);
                        lineLen = 0;
                    }

                    if (lineLen + word.Length > maxCharInLine)
                    {
                        output.Append("\n");
                        lineLen = 0;
                    }

                    output.Append(word + " ");

                    lineLen += word.Length + 1;
                }

                return output.ToString();
            }
        }

        /// <summary>
        ///     Loads a bitmap into OpenGL
        /// </summary>
        /// <param name="bitmap"></param>
        /// <returns></returns>
        public static KeyValuePair<int, Bitmap> LoadGlTexture(this Bitmap bitmap)
        {
            GL.Hint(HintTarget.PerspectiveCorrectionHint, HintMode.Nicest);

            GL.GenTextures(1, out int tex);
            GL.BindTexture(TextureTarget.Texture2D, tex);

            var data = bitmap.LockBits(new Rectangle(0, 0, bitmap.Width, bitmap.Height),
                ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);

            GL.TexImage2D(TextureTarget.Texture2D, 0, PixelInternalFormat.Rgba, data.Width, data.Height, 0,
                OpenTK.Graphics.OpenGL.PixelFormat.Bgra, PixelType.UnsignedByte, data.Scan0);
            bitmap.UnlockBits(data);

            GL.TexParameter(TextureTarget.Texture2D, TextureParameterName.TextureMinFilter,
                (int) TextureMinFilter.Nearest);
            GL.TexParameter(TextureTarget.Texture2D, TextureParameterName.TextureMagFilter,
                (int) TextureMagFilter.Linear);
            GL.TexParameter(TextureTarget.Texture2D, TextureParameterName.TextureWrapS,
                (int) TextureWrapMode.ClampToEdge);
            GL.TexParameter(TextureTarget.Texture2D, TextureParameterName.TextureWrapT,
                (int) TextureWrapMode.ClampToEdge);

            return new KeyValuePair<int, Bitmap>(tex, bitmap);
        }
    }
}