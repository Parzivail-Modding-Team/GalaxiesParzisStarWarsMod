int waterLevel = 36;
int treesPerChunk = 0;
boolean treesBelowWaterLevel = false;
int thLayer1 = 0.6;
int thLayer2 = 0.6;
ITerrainHeightmap terrain = new CompositeTerrain(
	new TerrainLayer(seed, TerrainLayer.Function.MidWave, TerrainLayer.Method.Add, 100, 80),
	new TerrainLayer(seed + 1, TerrainLayer.Function.Midpoint, TerrainLayer.Method.Add, 50, 20)
);