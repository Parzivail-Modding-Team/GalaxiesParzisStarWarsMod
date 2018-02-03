-- Controls the Y at which water starts
waterLevel = 15

function terrain(x, z)
	local h = octave(x / 200, z / 200, 6)
	return h * 270
end

function octave(x, z, numOctaves)
	if (numOctaves <= 1) then
		return f(noise(x, z)) / 2
	end
	return f(noise(x, z)) / 2 + octave((x + numOctaves * 100) * 2, (z + numOctaves * 100) * 2, numOctaves - 1) / 2
end

function f(h)
	return math.pow(h, 5)
end

function tree(x, y, z)
	return TREE_NONE
end