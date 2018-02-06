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

        public int WaterLevel { get; set; }

        private static readonly Dictionary<string, DataType> RequiredTopLevelObjects = new Dictionary<string, DataType>
        {
            {"waterLevel", DataType.Number},
            {"terrain", DataType.Function},
            {"tree", DataType.Function}
        };

        public void LoadScript(Script script, string scriptCode)
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
                        return;
                    }

                    Lumberjack.Error($"Couldn't find top-level object `{requiredTopLevelObject.Key}`");
                    return;
                }

                // Validate that the main method works
                var dummy = script.Call(script.Globals["terrain"], 0, 0);
                dummy = script.Call(script.Globals["tree"], 0, 0, 0);

                WaterLevel = (int) script.Globals.Get("waterLevel").Number;

                _script = script;
            }
            catch (Exception e)
            {
                Lumberjack.Error($"{e.GetType()}: {e.Message}");
            }
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
            var n = GetNoise(x, z) / 2;
            const double d = 0.001;
            return (GetNoise(x + d, z) - n) / d;
        }

        private double GetNoiseDz(double x, double z)
        {
            var n = GetNoise(x, z) / 2;
            const double d = 0.001;
            return (GetNoise(x, z + d) - n) / d;
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
