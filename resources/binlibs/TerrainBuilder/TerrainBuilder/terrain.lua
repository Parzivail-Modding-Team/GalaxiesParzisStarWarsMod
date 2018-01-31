-- Controls the Y at which water starts
waterLevel = 0

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

-- This function determines if a tree should be
-- present at the current (x, y, z) during the
-- decoration phase. Return `false` if no tree
-- should be present. Otherwise, return the tree
-- type ID (i.e. `1`, `2`, etc.)
function tree(x, y, z)
	-- In this example, no trees should be present
	-- underwater
	if (y < waterLevel)
		return false

	-- trees should be present roughly every 
	-- chunk, so only one in (16x16) blocks should
	-- contain a tree
	if (math.random(256) != 0)
		return
	end

	-- For example, ID-1 trees should be present where
	-- y > 100, but ID-0 trees should be present elsewhere.
	-- Simply put, use any means necessary to determine tree
	-- or no tree.
	if (y > 100)
		return 1
	else
		return 0
	end
end