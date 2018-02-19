-- Controls the Y at which water starts
waterLevel = 20

function terrain(x, z)
	return octave(x / 120, z / 120, 4) * 90
end

function octave(x, z, numOctaves)
	local n = worley(x, z) / 2
	if (numOctaves <= 1) then
		return n
	end
	return n + octave((x + numOctaves * 100) * 2, (z + numOctaves * 100) * 2, numOctaves - 1) / 2
end

function tree(x, y, z)
	return TREE_NONE
end