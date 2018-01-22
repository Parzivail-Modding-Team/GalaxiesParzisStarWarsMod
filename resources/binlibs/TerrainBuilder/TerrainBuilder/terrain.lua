-- Controls the Y at which water starts
waterLevel = 0

-- Controls the number of trees per chunk
treesPerChunk = 0
-- Controls if trees generate underwater or not
treesBelowWaterLevel = false

-- Controls the height value where later 2 stops and layer 1 starts
terrainLayer1Threshold = 0.9
-- Controls the height value where later 3 stops and layer 2 starts
terrainLayer2Threshold = 0.6

-- This is the main method that gets called to
-- find the height of the terrain at each point.
function terrain(x, z)

	-- First, we sample the noise with a scale factor of 100.
	-- The global function `noise` returns a sampled Simplex
	-- heightmap with values between 0 and 1.
	local h = noise(x / 100, z / 100);

	-- Then, we scale the resulting noise by 10, creating a
	-- maximum elevation change of 10 blocks, and return it
	-- back to the terrain generator.
	return h * 10;
end