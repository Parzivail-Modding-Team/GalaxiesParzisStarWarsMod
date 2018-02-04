-- Controls the Y at which water starts
waterLevel = 20

function terrain(x, z)
	return octave(x / 120, z / 120, 4) * 70
end

function deriv(x, z)
	local n = noise(x, z)

	local d = 0.1

	local dx = (noise(x + d, z) - n) / d
	local dz = (noise(x, z + d) - n) / d

	return dx, dz
end

function octave(x, z, numOctaves)
	local dx, dz = deriv(x, z)
	local n = (noise(x, z) + (dx * dz * 2)) / 2
	if (numOctaves <= 1) then
		return n
	end
	return n + octave((x + numOctaves * 100) * 2, (z + numOctaves * 100) * 2, numOctaves - 1) / 2
end

function tree(x, y, z)
	return TREE_NONE
end