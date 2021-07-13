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
        var flatness = ProcNoise.Noise(x / 200, z / 200);
        var noise = ProcNoise.OctaveNoise(x / 100, z / 100, 4) * 15 * flatness;

        var dx = ProcNoise.RawNoise(x / 20, z / 20 + 1000) * 5;
        var dz = ProcNoise.RawNoise(x / 20 + 1000, z / 20) * 5;

        dx += ProcNoise.RawNoise(x / 100, z / 100 + 1000) * 18;
        dz += ProcNoise.RawNoise(x / 100 + 1000, z / 100) * 18;

        var peaks = Math.Pow(1 - ProcNoise.Worley((x + dx) / 300 - 2000, (z + dz) / 300), 2);

        var peakHeight = Math.Min(peaks * 90 - 25, 40);
        
        if (peakHeight > 0)
        {
            // Surface hint: rocky
            return peakHeight + noise;
        }

        // Surface hint: sandy
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