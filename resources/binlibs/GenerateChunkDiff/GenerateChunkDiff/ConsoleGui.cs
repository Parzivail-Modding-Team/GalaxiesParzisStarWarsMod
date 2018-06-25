using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GenerateChunkDiff
{
    class ConsoleGui : List<ConsoleGuiComponent>
    {
        private readonly TimeSpan? _minRenderInterval;
        private DateTime _lastRender = DateTime.MinValue;

        public ConsoleGui(TimeSpan? minRenderInterval = null)
        {
            _minRenderInterval = minRenderInterval;
        }

        public void Render()
        {
            if (_minRenderInterval != null && DateTime.Now < _lastRender + _minRenderInterval)
                return;
            
            Console.ResetColor();
            Console.CursorVisible = false;

            foreach (var component in this)
                component.Render();

            if (_minRenderInterval != null)
                _lastRender = DateTime.Now;
        }
    }

    internal class ConsoleGuiComponent
    {
        private int _maxWidth;
        private int _width;

        public int X { get; set; }
        public int Y { get; set; }
        public int Height { get; set; }

        public int Width
        {
            get => _width;
            set
            {
                _width = value;
                if (value > _maxWidth)
                    _maxWidth = value;
            }
        }

        public ConsoleColor ForegroundColor { get; set; }
        public ConsoleColor BackgroundColor { get; set; }

        public ConsoleGuiComponent(int x, int y, int width, int height)
        {
            X = x;
            Y = y;
            Width = _maxWidth = width;
            Height = height;
            ForegroundColor = Console.ForegroundColor;
            BackgroundColor = Console.BackgroundColor;
        }

        public virtual void Render()
        {
            Console.ForegroundColor = ForegroundColor;
            Console.BackgroundColor = BackgroundColor;

            for (var dy = 0; dy < Height; dy++)
            {
                Console.SetCursorPosition(X, Y + dy);
                Console.Write("".PadRight(_maxWidth, ' '));
            }
        }
    }

    internal class ConsoleGuiLabel : ConsoleGuiComponent
    {
        private string _text;
        private object _value;

        public string Text
        {
            get => _text;
            set
            {
                _text = value;
                Width = string.Format(Text, Value).Length;
            }
        }

        public object Value
        {
            get => _value;
            set
            {
                _value = value;
                Width = string.Format(Text, value).Length;
            }
        }

        public ConsoleGuiLabel(int x, int y, string text) : base(x, y, text.Length, 1)
        {
            Text = text;
        }

        public override void Render()
        {
            base.Render();

            Console.SetCursorPosition(X, Y);
            Console.Write(Text, Value);
        }
    }

    internal class ConsoleGuiProgressBar : ConsoleGuiComponent
    {
        public float Min { get; set; }
        public float Max { get; set; }
        public float Value { get; set; }

        private static readonly char[] BlockPercentFill =
        {
            ' ',
            '░',
            '▒',
            '▓'
        };

        public ConsoleGuiProgressBar(int x, int y, int width, float min, float max) : base(x, y, width, 1)
        {
            Min = min;
            Max = max;
        }

        public override void Render()
        {
            base.Render();

            var p = (Value - Min) / (Max - Min);
            var w = (int) Math.Floor(p * Width);
            var fractW = (int) Math.Floor((p * Width - w) * BlockPercentFill.Length);

            var str = $"{Math.Round(p * 100):N0}%";
            var strLeft = str.Length > w ? str.Substring(0, w) : str;
            var strRight = str.Length > w ? str.Substring(w, str.Length - w) : "";

            Console.SetCursorPosition(X, Y);

            Console.ForegroundColor = BackgroundColor;
            Console.BackgroundColor = ForegroundColor;
            Console.Write(strLeft.PadRight(w));

            Console.ForegroundColor = ForegroundColor;
            Console.BackgroundColor = BackgroundColor;
            Console.Write((strRight.Length == 0 ? BlockPercentFill[fractW].ToString() : "") + strRight.PadRight(Width - w));
        }
    }

    internal class ConsoleGuiAreaChart : ConsoleGuiComponent
    {
        public float Min { get; set; }
        public float Max { get; set; }
        public List<float> Values { get; set; }

        public ConsoleGuiAreaChart(int x, int y, int width, int height, float min, float max) : base(x, y, width, height)
        {
            Min = min;
            Max = max;
            Values = new List<float>();
        }

        public override void Render()
        {
            base.Render();

            while (Values.Count < Width)
                Values.Insert(0, Min - 100);

            while (Values.Count > Width)
                Values.RemoveAt(0);

            Max = Values.Max();

            var sb = new StringBuilder();

            for (var y = 0; y < Height; y++)
            {
                foreach (var value in Values)
                {
                    var p = (value - Min) / (Max - Min);
                    var w = (int) Math.Round(p * Height);

                    sb.Append(w >= Height - y ? "█" : " ");
                }
            }

            Console.SetCursorPosition(X, Y);
            Console.Write(sb.ToString());
            
            Console.SetCursorPosition(X, Y);
            Console.Write(Max.ToString("N0"));

            Console.SetCursorPosition(X, Y + Height - 1);
            Console.Write(Min.ToString("N0"));

            var last = Values.Last().ToString("N0");
            Console.SetCursorPosition(X + Width - last.Length, Y + Height - 1);
            Console.Write(last);
        }
    }
}
