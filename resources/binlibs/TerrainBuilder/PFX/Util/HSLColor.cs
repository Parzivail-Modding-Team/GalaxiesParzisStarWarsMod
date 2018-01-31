using System.Drawing;

namespace PFX.Util
{
    public class HslColor
    {
        private const double Scale = 1.0;

        // Private data members below are on scale 0-1
        // They are scaled for use externally based on scale
        private double _hue = 1.0;
        private double _luminosity = 1.0;
        private double _saturation = 1.0;

        public HslColor()
        {
        }

        public HslColor(Color color)
        {
            SetRgb(color.R, color.G, color.B);
        }

        public HslColor(int red, int green, int blue)
        {
            SetRgb(red, green, blue);
        }

        public HslColor(double hue, double saturation, double brightness)
        {
            Hue = hue;
            Saturation = saturation;
            Brightness = brightness;
        }

        public double Hue
        {
            get => _hue * Scale;
            set => _hue = CheckRange(value / Scale);
        }

        public double Saturation
        {
            get => _saturation * Scale;
            set => _saturation = CheckRange(value / Scale);
        }

        public double Brightness
        {
            get => _luminosity * Scale;
            set => _luminosity = CheckRange(value / Scale);
        }

        private static double CheckRange(double value)
        {
            if (value < 0.0)
                value = 0.0;
            else if (value > 1.0)
                value = 1.0;
            return value;
        }

        public override string ToString()
        {
            return $"H: {Hue:#0.##} S: {Saturation:#0.##} L: {Brightness:#0.##}";
        }

        public string ToRgbString()
        {
            Color color = this;
            return $"R: {color.R:#0.##} G: {color.G:#0.##} B: {color.B:#0.##}";
        }

        public void SetRgb(int red, int green, int blue)
        {
            HslColor hslColor = Color.FromArgb(red, green, blue);
            _hue = hslColor._hue;
            _saturation = hslColor._saturation;
            _luminosity = hslColor._luminosity;
        }

        #region Casts to/from System.Drawing.Color

        public static implicit operator Color(HslColor hslColor)
        {
            double r = 0, g = 0, b = 0;
            if (hslColor._luminosity != 0)
                if (hslColor._saturation == 0)
                {
                    r = g = b = hslColor._luminosity;
                }
                else
                {
                    var temp2 = GetTemp2(hslColor);
                    var temp1 = 2.0 * hslColor._luminosity - temp2;

                    r = GetColorComponent(temp1, temp2, hslColor._hue + 1.0 / 3.0);
                    g = GetColorComponent(temp1, temp2, hslColor._hue);
                    b = GetColorComponent(temp1, temp2, hslColor._hue - 1.0 / 3.0);
                }

            return Color.FromArgb((int) (255 * r), (int) (255 * g), (int) (255 * b));
        }

        private static double GetColorComponent(double temp1, double temp2, double temp3)
        {
            temp3 = MoveIntoRange(temp3);
            if (temp3 < 1.0 / 6.0)
                return temp1 + (temp2 - temp1) * 6.0 * temp3;
            if (temp3 < 0.5)
                return temp2;
            if (temp3 < 2.0 / 3.0)
                return temp1 + (temp2 - temp1) * (2.0 / 3.0 - temp3) * 6.0;
            return temp1;
        }

        private static double MoveIntoRange(double temp3)
        {
            if (temp3 < 0.0)
                temp3 += 1.0;
            else if (temp3 > 1.0)
                temp3 -= 1.0;
            return temp3;
        }

        private static double GetTemp2(HslColor hslColor)
        {
            double temp2;
            if (hslColor._luminosity < 0.5) //<=??
                temp2 = hslColor._luminosity * (1.0 + hslColor._saturation);
            else
                temp2 = hslColor._luminosity + hslColor._saturation - hslColor._luminosity * hslColor._saturation;
            return temp2;
        }

        public static implicit operator HslColor(Color color)
        {
            var hslColor = new HslColor
            {
                _hue = color.GetHue() / 360.0,
                _luminosity = color.GetBrightness(),
                _saturation = color.GetSaturation()
            };
            // we store hue as 0-1 as opposed to 0-360 
            return hslColor;
        }

        #endregion
    }
}