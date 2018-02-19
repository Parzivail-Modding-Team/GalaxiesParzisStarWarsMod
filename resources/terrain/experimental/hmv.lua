-- Controls the Y at which water starts
waterLevel = 0

invGoldRatio = 0.61803398875

function fif(a, b, c)
	if (a) then
		return b
	end
	return c
end

function terrain(x, z)
	local px = x
	local py = z
	local v = octWorley(x / 50, z / 50, 4)

	--local v = hashA(x, z)
	--return fract(math.sin((py * 7441.35 - 4113.21 * math.sin(px) + py * 1727.93) * 1291.27) * 2853 + invGoldRatio) * 255

	--return fif(v > 0.49 and v < 0.51, 1, 0) * 255
	return v * 255
end

function tree(x, y, z)
	return TREE_NONE
end