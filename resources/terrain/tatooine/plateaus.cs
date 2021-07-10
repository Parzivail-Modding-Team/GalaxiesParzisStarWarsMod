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
        var h = ProcNoise.OctaveNoise(x / 500, z / 500, 5);

        h = Math.Pow(h, 2) - 0.3;

        var surfaceNoise = ProcNoise.OctaveWorley(x / 100, z / 100, 5);

        if (h > 0.15)
        {
            // Surface hint: stratified sediment
            return (0.5 + 0.15 * surfaceNoise) * 60;
        }
        
        // Surface hint: loose rubble
        return h * 180;
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