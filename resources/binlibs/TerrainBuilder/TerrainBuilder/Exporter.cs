using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrainBuilder
{
    class Exporter
    {
        public static void Export(TerrainLayerList terrainLayerList, string filename)
        {
            var i = 0;
            var enumerable = terrainLayerList.Layers as IList<TerrainLayer> ?? terrainLayerList.Layers.ToList();
            var output = $"int waterLevel = {(int) terrainLayerList.nudWaterLevel.Value};\n";
            output += $"int treesPerChunk = {(int) terrainLayerList.nudTreesPerChunk.Value};\n";
            output +=
                $"boolean treesBelowWaterLevel = {terrainLayerList.cbSubmarineTrees.Checked.ToString().ToLowerInvariant()};\n";
            output += $"int thLayer1 = {(double)terrainLayerList.nudLayer1.Value};\n";
            output += $"int thLayer2 = {(double)terrainLayerList.nudLayer2.Value};\n";
            output += enumerable.Aggregate("ITerrainHeightmap terrain = new CompositeTerrain(\n", (current, terrainLayer) => current + $"\tnew TerrainLayer(seed{(i > 0 ? " + " + i : "")}, TerrainLayer.Function.{terrainLayer.Function}, TerrainLayer.Method.{terrainLayer.Method}, {terrainLayer.Scale}, {terrainLayer.Range}){(++i == enumerable.Count ? "" : ",")}\n") + ");";
            File.WriteAllText(filename, output);
        }
    }
}
