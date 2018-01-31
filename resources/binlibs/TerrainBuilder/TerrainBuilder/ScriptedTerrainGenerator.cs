using System;
using System.Collections.Generic;
using MoonSharp.Interpreter;
using PFX;
using PFX.Util;

namespace TerrainBuilder
{
    public class ScriptedTerrainGenerator
    {
        private OpenSimplexNoise _noise = new OpenSimplexNoise(0);
        private Script _script;

        public int WaterLevel { get; set; }
        public int TreesPerChunk { get; set; }
        public bool TreesBelowWaterLevel { get; set; }

        private static readonly Dictionary<string, DataType> RequiredTopLevelObjects = new Dictionary<string, DataType>
        {
            {"waterLevel", DataType.Number},
            {"treesPerChunk", DataType.Number},
            {"treesBelowWaterLevel", DataType.Boolean},
            {"terrain", DataType.Function}
        };

        public void LoadScript(Script script, string scriptCode)
        {
            script.Globals["noise"] = (Func<double, double, double>)GetNoise;
            script.Globals["rawnoise"] = (Func<double, double, double>)GetRawNoise;

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

                WaterLevel = (int) script.Globals.Get("waterLevel").Number;
                TreesPerChunk = (int) script.Globals.Get("treesPerChunk").Number;
                TreesBelowWaterLevel = script.Globals.Get("treesBelowWaterLevel").Boolean;

                _script = script;
            }
            catch (Exception e)
            {
                Lumberjack.Error($"{e.GetType()}: {e.Message}");
            }
        }

        private double GetNoise(double x, double z)
        {
            return (_noise.Eval(x, z) + 1) / 2;
        }

        private double GetRawNoise(double x, double z)
        {
            return _noise.Eval(x, z);
        }

        public void SetSeed(long nudSeedValue)
        {
            _noise = new OpenSimplexNoise(nudSeedValue);
        }

        public double GetValue(double x, double z)
        {
            return _script?.Globals["terrain"] == null ? 0 : _script.Call(_script.Globals["terrain"], x, z).Number;
        }
    }
}
