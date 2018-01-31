using System;
using System.Collections.Concurrent;
using System.Drawing;
using System.Linq;
using OpenTK.Graphics.OpenGL;
using PFX.BmFont;

namespace PFX
{
    public class Sparkline : ConcurrentQueue<float>
    {
        private readonly BitmapFont _font;
        private readonly string _label;
        private readonly float _maxValue;
        private readonly SparklineStyle _style;

        private readonly object _syncObject = new object();

        public Sparkline(BitmapFont font, string label, int maxEntries, float maxValue, SparklineStyle style)
        {
            _font = font;
            _label = label;
            MaxEntries = maxEntries;
            _maxValue = maxValue;
            _style = style;
        }

        public int MaxEntries { get; }

        public void Render(params object[] formatArgs)
        {
            GL.PushMatrix();
            var label = string.Format(_label, formatArgs);
            var scalar = _font.Common.LineHeight / _maxValue;

            GL.Disable(EnableCap.Lighting);
            GL.Disable(EnableCap.Texture2D);
            GL.Color3(Color.White);

            GL.LineWidth(1);
            switch (_style)
            {
                case SparklineStyle.Area:
                    GL.Begin(PrimitiveType.Lines);
                    for (var i = 0; i < Count; i++)
                    {
                        GL.Vertex2(i, _font.Common.LineHeight);
                        GL.Vertex2(i, _font.Common.LineHeight - scalar * this.ElementAt(i) - 1);
                    }

                    GL.End();
                    break;
                case SparklineStyle.Line:
                    GL.Begin(PrimitiveType.LineStrip);
                    for (var i = 0; i < Count; i++)
                        GL.Vertex2(i, _font.Common.LineHeight - scalar * this.ElementAt(i));
                    GL.End();
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }

            GL.Translate(MaxEntries + 2, 0, 0);

            GL.Enable(EnableCap.Blend);
            GL.Enable(EnableCap.Texture2D);
            _font.RenderString(label);
            GL.Disable(EnableCap.Texture2D);

            GL.Enable(EnableCap.Lighting);
            GL.Disable(EnableCap.Blend);
            GL.PopMatrix();
        }

        public new void Enqueue(float obj)
        {
            base.Enqueue(obj);
            lock (_syncObject)
            {
                while (Count > MaxEntries)
                    TryDequeue(out var outObj);
            }
        }

        public enum SparklineStyle
        {
            Area,
            Line
        }
    }
}