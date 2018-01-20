int waterLevel = 36;
int treesPerChunk = 0;
boolean treesBelowWaterLevel = false;
int thLayer1 = 0.6;
int thLayer2 = 0.6;
ITerrainHeightmap terrain = new CompositeTerrain(
	new TerrainLayer(seed, TerrainLayer.Function.MidWave, TerrainLayer.Method.Add, 100, 70),
	new TerrainLayer(seed + 1, TerrainLayer.Function.Turbulent, TerrainLayer.Method.Multiply, 200, 1),
	new TerrainLayer(seed + 2, TerrainLayer.Function.Klump, TerrainLayer.Method.Add, 50, 20),
	new TerrainLayer(seed + 3, TerrainLayer.Function.Constant, TerrainLayer.Method.Add, 200, 30)
);