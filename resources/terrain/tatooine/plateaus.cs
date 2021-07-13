using System;
using TerrainGenCore;

/// <summary>
/// Stores a collection of methods to generate terrain.
/// </summary>
[TerrainProvider]
class TerrainGenerator
{
    /// <summary>
    /// Returns the height of the terrain at a given point
    /// </summary>
    /// <param name="x">The x coordiate of the point to sample</param>
    /// <param name="z">The z coordiate of the point to sample</param>
    /// <returns>A height between 0 and 255, inclusive</returns>
    public double GetTerrain(double x, double z)
    {
        var dX = ProcNoise.RawNoise(x / 50, z / 50 + 1000) * 10;
        var dZ = ProcNoise.RawNoise(x / 50 + 1000, z / 50) * 10;
        var h = ProcNoise.OctaveNoise((x + dX) / 500, (z + dZ) / 500, 5);

        h = Math.Pow(h, 2) - 0.2;

        var surfaceNoise = ProcNoise.OctaveNoise(x / 100, z / 100, 2) * 0.5;

        if (h > 0.15)
        {
            // Surface hint: stratified sediment
            return 50 + (0.5 + 0.15 * surfaceNoise) * 60;
        }

        var floorNoise = ProcNoise.RawNoise(x / 150, z / 150);
        
        // Surface hint: loose rubble
        return 50 + Math.Max(h * 180, floorNoise * 2);
    }

    /// <summary>
    /// Gets the water level for the entire terrain
    /// </summary>
    /// <returns>A number between 0 and 255, inclusive</returns>
    public int GetWaterLevel()
    {
        return 0;
    }
}