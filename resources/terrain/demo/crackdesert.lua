-- Controls the Y at which water starts
waterLevel = 0

-- This is the main method that gets called to
-- find the height of the terrain at each point.
function terrain(x, z)

	local s = 2
	local h = get(x / s, z / s);
	local d = 5 * s
	
	local blur = 0;
	blur = blur + get(x / s - d, z / s - d)
	blur = blur + get(x / s - d, z / s + d)
	
	blur = blur + get(x / s + d, z / s - d)
	blur = blur + get(x / s + d, z / s + d)
	blur = blur / 4
	
	h = 1 - (h - blur);
	
	local a = 8 - math.pow(s - 1, 2);
	local b = -3.2 + math.sqrt(s - 1);
	
	h = 1 - 1 / (2 * h) - 0.42
	h = h * 20
	
	if (h > 1) then
		h = 1
	end
	
	if (h < 0) then
		h = 0
	end
	
	local j = octave(x / 200, z / 200, 6) * 90

	-- Then, we scale the resulting noise by 10, creating a
	-- maximum elevation change of 10 blocks, and return it
	-- back to the terrain generator.
	--return (h * 0.5 + noise(x / 200 + 2000, z / 200) * 0.5) * (j + 10);
	return get(x / s, z / s) * 50
end

function octave(x, z, numOctaves)
	if (numOctaves <= 1) then
		return noise(x, z) / 2
	end
	return noise(x, z) / 2 + octave((x + numOctaves * 100) * 2, (z + numOctaves * 100) * 2, numOctaves - 1) / 2
end

function get(x, z)
	local offsetX = noise(x / 10, z / 10 - 1000) / 10;
	local offsetY = noise(x / 10 + 1000, z / 10) / 10;

	-- First, we sample the noise with a scale factor of 100.
	-- The global function `noise` returns a sampled Simplex
	-- heightmap with values between 0 and 1.
	return worley(x / 50 + offsetX, z / 50 + offsetY);
end

function tree(x, y, z)
	return TREE_NONE;
end