-- Controls the Y at which water starts
waterLevel = 15

function terrain(x, z)
	local h = octave(x / 200, z / 200, 5)
	return h * 200
end

function nnoise(x, z)
	local low = 1
	for i=-1, 1 do
		for j=-1, 1 do
			local n = noise(x + i * 2, z + j * 2)
			if (n < low) then
				low = n
			end
		end
	end
	return math.sqrt(math.pow(noise(x, z), 3) - math.pow(low, 3))
end

function octave(x, z, numOctaves)
	if (numOctaves <= 1) then
		return nnoise(x, z) / 2
	end
	return nnoise(x, z) / 2 + octave((x + numOctaves * 100) * 2, (z + numOctaves * 100) * 2, numOctaves - 1) / 2
end

function tree(x, y, z)
	return TREE_NONE
end