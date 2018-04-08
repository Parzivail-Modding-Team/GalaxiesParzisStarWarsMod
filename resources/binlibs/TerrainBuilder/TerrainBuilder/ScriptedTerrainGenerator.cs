using System;
using System.Collections.Generic;
using MoonSharp.Interpreter;
using PFX;
using PFX.Util;

namespace TerrainBuilder
{
    public class ScriptedTerrainGenerator
    {
        private OpenSimplexNoise _noise = new OpenSimplexNoise();
        private InfiniteWorleyNoise _worley = new InfiniteWorleyNoise();
        private Script _script;
        private long _seed;

        public int WaterLevel { get; set; }

        private static readonly Dictionary<string, DataType> RequiredTopLevelObjects = new Dictionary<string, DataType>
        {
            {"waterLevel", DataType.Number},
            {"terrain", DataType.Function},
            {"tree", DataType.Function}
        };

        public bool LoadScript(Script script, string scriptCode)
        {
            // 2d noise
            script.Globals["noise"] = (Func<double, double, double>)GetNoise;
            script.Globals["rawnoise"] = (Func<double, double, double>)GetRawNoise;

            // 3d noise
            script.Globals["noise3"] = (Func<double, double, double, double>)GetNoise;
            script.Globals["rawnoise3"] = (Func<double, double, double, double>)GetRawNoise;

            // 4d noise
            script.Globals["noise4"] = (Func<double, double, double, double, double>)GetNoise;
            script.Globals["rawnoise4"] = (Func<double, double, double, double, double>)GetRawNoise;

            // 2d worley noise
            script.Globals["worley"] = (Func<double, double, double>)GetWorleyNoise;
            script.Globals["rawworley"] = (Func<double, double, double>)GetRawWorleyNoise;

            // 3d worley noise
            script.Globals["worley3"] = (Func<double, double, double, double>)GetWorleyNoise;
            script.Globals["rawworley3"] = (Func<double, double, double, double>)GetRawWorleyNoise;

            // 2d noise derivs
            script.Globals["noiseDx"] = (Func<double, double, double>)GetNoiseDx;
            script.Globals["noiseDz"] = (Func<double, double, double>)GetNoiseDz;

            // noise octaves
            script.Globals["octNoise"] = (Func<double, double, int, double>)GetOctaveNoise;
            script.Globals["octWorley"] = (Func<double, double, int, double>)GetOctaveWorley;
            script.Globals["octInvWorley"] = (Func<double, double, int, double>)GetOctaveInvWorley;

            // 2d hash functions
            script.Globals["hashA"] = (Func<double, double, double>)GetHashA;
            script.Globals["hashB"] = (Func<double, double, double>)GetHashB;

            // constants
            script.Globals["TREE_NONE"] = 0;
            script.Globals["TREE_MC"] = 1;

            try
            {
                script.DoString(scriptCode);

                foreach (var requiredTopLevelObject in RequiredTopLevelObjects)
                {
                    if (script.Globals[requiredTopLevelObject.Key] != null)
                    {
                        if (script.Globals.Get(requiredTopLevelObject.Key).Type == requiredTopLevelObject.Value)
                            continue;
                        Lumberjack.Error($"Expected top-level object `{requiredTopLevelObject.Key}` to be of type {requiredTopLevelObject.Value}, was instead {script.Globals.Get(requiredTopLevelObject.Key).Type}.");
                        return false;
                    }

                    Lumberjack.Error($"Couldn't find top-level object `{requiredTopLevelObject.Key}`");
                    return false;
                }

                // Validate that the main method works
                var dummy = script.Call(script.Globals["terrain"], 0, 0);
                dummy = script.Call(script.Globals["tree"], 0, 0, 0);

                WaterLevel = (int) script.Globals.Get("waterLevel").Number;

                _script = script;
                return true;
            }
            catch (Exception e)
            {
                Lumberjack.Error($"{e.GetType()}: {e.Message}");
                return false;
            }
        }

        private double GetHashA(double x, double z)
        {
            return MathUtil.Fract(Math.Sin(MathUtil.Seed(x - 173.37, _seed) * 7441.35 - 4113.21 * Math.Cos(x * z) + MathUtil.Seed(z - 1743.7, _seed) * 1727.93 * 1291.27) * 2853.85 + MathUtil.OneOverGoldenRatio);
        }

        private double GetHashB(double x, double z)
        {
            return MathUtil.Fract(Math.Cos(MathUtil.Seed(z - 143.37, _seed) * 4113.21 - 2853.85 * Math.Sin(x * z) + MathUtil.Seed(x - 743.37, _seed) * 1291.27 * 1727.93) * 4113.21 + MathUtil.OneOverGoldenRatio);
        }

        private double GetOctaveNoise(double x, double z, int octaves)
        {
            var n = GetNoise(x, z) / 2;
            if (octaves <= 1)
                return n;
            return n + GetOctaveNoise((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
        }

        private double GetOctaveWorley(double x, double z, int octaves)
        {
            var n = GetWorleyNoise(x, z) / 2;
            if (octaves <= 1)
                return n;
            return n + GetOctaveNoise((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
        }

        private double GetOctaveInvWorley(double x, double z, int octaves)
        {
            var n = (1 - GetWorleyNoise(x, z)) / 2;
            if (octaves <= 1)
                return n;
            return n + GetOctaveInvWorley((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
        }

        private double GetWorleyNoise(double x, double z)
        {
            return _worley.Eval(x, z);
        }

        private double GetRawWorleyNoise(double x, double z)
        {
            return _worley.Eval(x, z) * 2 - 1;
        }

        private double GetWorleyNoise(double x, double y, double z)
        {
            return _worley.Eval(x, y, z);
        }

        private double GetRawWorleyNoise(double x, double y, double z)
        {
            return _worley.Eval(x, y, z) * 2 - 1;
        }

        private double GetNoiseDx(double x, double z)
        {
            var n = GetNoise(x, z);
            const double d = 0.001;
            return GetNoise(x + d, z) - n;
        }

        private double GetNoiseDz(double x, double z)
        {
            var n = GetNoise(x, z);
            const double d = 0.001;
            return GetNoise(x, z + d) - n;
        }

        private double GetNoise(double x, double z)
        {
            return (_noise.Eval(x, z) + 1) / 2;
        }

        private double GetNoise(double x, double y, double z)
        {
            return (_noise.Eval(x, y, z) + 1) / 2;
        }

        private double GetNoise(double x, double y, double z, double w)
        {
            return (_noise.Eval(x, y, z, w) + 1) / 2;
        }

        private double GetRawNoise(double x, double z)
        {
            return _noise.Eval(x, z);
        }

        private double GetRawNoise(double x, double y, double z)
        {
            return _noise.Eval(x, y, z);
        }

        private double GetRawNoise(double x, double y, double z, double w)
        {
            return _noise.Eval(x, y, z, w);
        }

        public void SetSeed(long nudSeedValue)
        {
            _seed = nudSeedValue;
            _noise = new OpenSimplexNoise(nudSeedValue);
            _worley = new InfiniteWorleyNoise(nudSeedValue);
        }

        public double GetValue(double x, double z)
        {
            var value = _script?.Globals["terrain"] == null ? 0 : _script.Call(_script.Globals["terrain"], x, z).Number;

            if (value < 0 || double.IsNaN(value))
                value = 0;
            if (value > 255)
                value = 255;

            return value;
        }

        public int GetTree(double x, double y, double z)
        {
            return _script?.Globals["tree"] == null ? 0 : (int)_script.Call(_script.Globals["tree"], x, y, z).Number;
        }
    }
}
