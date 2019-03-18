-- Controls the Y at which water starts
waterLevel = 0

function circularize(x)
	return 1 - math.sqrt(1 - math.pow(x, 2))
	--return -4.1 * math.pow(x, 3) + 5.3 * math.pow(x, 2) - 0.2 * x
end

-- This is the main method that gets called to
-- find the height of the terrain at each point.
function terrain(x, z)

	-- First, we sample the noise with a scale factor of 100.
	-- The global function `noise` returns a sampled Simplex
	-- heightmap with values between 0 and 1.
	local j = octNoise(x / 75, z / 75, 4)
	local h = worley3(x / 200, z / 200, j) * 3
	if (h < 1) then
		h = circularize(h)
	else 
		--[[local d = 0.4
		if (h < 1 + d) then
			h = 0.5 / d - circularize(h - d)
		else
			h = 0
		end]]--
		h = 1
	end
	

	-- Then, we scale the resulting noise by 10, creating a
	-- maximum elevation change of 10 blocks, and return it
	-- back to the terrain generator.
	return h * 30 + octNoise(x / 40, z / 40, 5) * 30
end

function tree(x, y, z)
	return TREE_NONE
end