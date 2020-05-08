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
		var dx = ProcNoise.Noise(x / 5, z / 5 + 3000) * 10;
		var dz = ProcNoise.Noise(x / 5 + 3000, z / 5) * 10;

		var peaks = ProcNoise.OctaveInvWorley((x + dx) / 100 - 3000, (z + dz) / 100, 3) * 1.2;
		var noise = Math.Max(peaks * 50, 35) - 30;

		noise *= 5 * (ProcNoise.Noise(x / 70, z / 70) + 0.1);

		noise += ProcNoise.OctaveNoise(x / 50, z / 50 - 3000, 3) * 30;

        return noise;
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