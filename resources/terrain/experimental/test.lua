-- Controls the Y at which water starts
waterLevel = 0

function terrain(x, z)
	return octave(x / 200, z / 200, 6) * 80
	--return noise(x / 200, z / 200) * 80
end

function tree(x, y, z)
	-- if (math.random(200) == 1) then
	-- 	return TREE_REDWOOD
	-- end
	return TREE_NONE
end

function octave(x, z, numOctaves)
	if (numOctaves == 1) then
		return noise(x, z)
	end
	return noise(x, z) + octave(x * 2, z * 2, numOctaves - 1) / 2
end

function turbulent(x, z)
	return -0.5 + math.abs(rawnoise(x, z)) * 2
end

function ncturbulent(x, z)
	return math.abs(rawnoise(x, z))
end

function midpoint(x, z)
	return -math.abs(2 * rawnoise(x, z) - 1) + 1
end

function filmmelt(x, z)
	local t = rawnoise(x, z);
	local a = math.pow(math.exp(1), 54 * (t - 0.81)) + 1;
    local b = math.pow(math.exp(1), 10 * (-t + 0.19)) + 1;
    local c = math.pow(45, math.pow(-(5 * t - 2.5), 2));
    return (1 / (a * b) - c / 20) / 0.9969;
end

function warble(x, z)
	return math.abs(noise(x, z) * math.sin(100 * noise(x, z)))
end

function klump(x, z)
	return noise(x, z) * 1.7265 * math.sin(math.pi * noise(x, z))
end

function hilopass(x, z)
	return math.pow(noise(x, z), math.sin(math.pi * noise(x, z)))
end

function midwave(x, z)
	return noise(x, z) + (math.sin(noise(x, z) * math.pi * 2) + 1) / 2 - 0.5
end