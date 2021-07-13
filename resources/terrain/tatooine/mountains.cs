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
        var domainWarpScale = ProcNoise.RawNoise(x / 50 - 1000, z / 50 - 1000);
        var dx = ProcNoise.RawNoise(x / 40 + 1000, z / 40) * domainWarpScale * 20;
        var dz = ProcNoise.RawNoise(x / 40, z / 40 + 1000) * domainWarpScale * 20;
        var h = ProcNoise.OctaveNoise((x + dx) / 200, (z + dz) / 200, 6) * 2;

        h *= Math.Pow(1.2 - ProcNoise.OctaveWorley(x / 300, z / 300, 1), 2);

        return h * 50;
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