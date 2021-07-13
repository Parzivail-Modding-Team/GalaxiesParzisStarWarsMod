using System;
using TerrainGenCore;

/// <summary>
/// Stores a collection of methods to generate terrain.
/// </summary>
[TerrainProvider]
class TerrainGenerator
{
	private static double getWorleyDomainWarped(double x, double z)
	{
		double offsetX = ProcNoise.Noise(x / 10, z / 10 + 1000) / 10;
		double offsetY = ProcNoise.Noise(x / 10 + 1000, z / 10) / 10;

		return ProcNoise.Worley(x / 50 + offsetX, z / 50 + offsetY);
	}

    /// <summary>
    /// Returns the height of the terrain at a given point
    /// </summary>
    /// <param name="x">The x coordiate of the point to sample</param>
    /// <param name="z">The z coordiate of the point to sample</param>
    /// <returns>A height between 0 and 255, inclusive</returns>
    public double GetTerrain(double x, double z)
    {
        //x *= 10;
        //z *= 10;
        
        var nx = ProcNoise.OctaveNoise(x / 1500 + 1000, z / 1500, 8);
        var nz = ProcNoise.OctaveNoise(x / 1500, z / 1500 + 1000, 8);
        var noise = ProcNoise.Worley(x / 500 + nx, z / 500 + nz);

        var d = 0.7;
        var winding = Math.Sqrt(noise);
        var basin = (1 - (winding - d) / (1 - d));

        double height = 0;

        if (winding < d)
            height = (basin - 1) * 10 * ProcNoise.OctaveNoise(x / 500, z / 500, 6);
        else
            height = 50 * Math.Pow(basin, 10) - 50;

        return 80 + height;
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