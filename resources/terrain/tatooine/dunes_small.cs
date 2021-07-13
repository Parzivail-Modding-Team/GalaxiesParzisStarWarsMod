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
        var dX = ProcNoise.RawNoise(x / 100 + 1000, z / 100) * 20;
        var dZ = ProcNoise.RawNoise(x / 100, z / 100 + 1000) * 20;

		var noise = ProcNoise.Noise(x / 400 - 3000, z / 400) * 18;

		noise += ProcNoise.Noise(x / 80, z / 80 - 3000) * 25

		* Math.Pow((1 - Math.Abs(ProcNoise.RawNoise((x + dX) / 150, (z + dZ) / 150 + 3000))), 0.75);

        return 50 + noise;
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