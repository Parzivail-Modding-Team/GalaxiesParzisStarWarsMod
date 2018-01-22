using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms.VisualStyles;
using MoonSharp.Interpreter;

namespace TerrainBuilder
{
    public class ScriptedTerrainGenerator
    {
        private OpenSimplexNoise _noise = new OpenSimplexNoise(0);
        private Script _script;

        public int WaterLevel { get; set; }
        public int TreesPerChunk { get; set; }
        public bool TreesBelowWaterLevel { get; set; }
        public float ThLayer1 { get; set; }
        public float ThLayer2 { get; set; }

        private static readonly Dictionary<string, DataType> RequiredTopLevelObjects = new Dictionary<string, DataType>
        {
            {"waterLevel", DataType.Number},
            {"treesPerChunk", DataType.Number},
            {"treesBelowWaterLevel", DataType.Boolean},
            {"terrainLayer1Threshold", DataType.Number},
            {"terrainLayer2Threshold", DataType.Number},
            {"terrain", DataType.Function}
        };

        public void LoadScript(Script script, string scriptCode)
        {
            script.Globals["noise"] = (Func<double, double, double>)GetNoise;

            try
            {
                script.DoString(scriptCode);

                foreach (var requiredTopLevelObject in RequiredTopLevelObjects)
                {
                    if (script.Globals[requiredTopLevelObject.Key] != null)
                    {
                        if (script.Globals.Get(requiredTopLevelObject.Key).Type == requiredTopLevelObject.Value)
                            continue;
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine($"Expected top-level object `{requiredTopLevelObject.Key}` to be of type {requiredTopLevelObject.Value}, was instead {script.Globals.Get(requiredTopLevelObject.Key).Type}.");
                        Console.ResetColor();
                        return;
                    }

                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine($"Couldn't find top-level object `{requiredTopLevelObject.Key}`");
                    Console.ResetColor();
                    return;
                }

                // Validate that the main method works
                var dummy = script.Call(script.Globals["terrain"], 0, 0);

                WaterLevel = (int) script.Globals.Get("waterLevel").Number;
                TreesPerChunk = (int) script.Globals.Get("treesPerChunk").Number;
                TreesBelowWaterLevel = script.Globals.Get("treesBelowWaterLevel").Boolean;
                ThLayer1 = (float) script.Globals.Get("terrainLayer1Threshold").Number;
                ThLayer2 = (float) script.Globals.Get("terrainLayer2Threshold").Number;

                _script = script;
            }
            catch (Exception e)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine($"{e.GetType()}: {e.Message}");
                Console.ResetColor();
            }
        }

        private double GetNoise(double x, double z)
        {
            return (_noise.eval(x, z) + 1) / 2;
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
