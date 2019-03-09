require "math"

waterLevel = 0

function terrain(x, z)
	local h = octWorley(x / 150, z / 150, 4);
	local h2 = octWorley(x / 150, z / 150, 5);
	return math.max(h * 80, h2 * 80)
end

function tree(x, y, z)
	return TREE_NONE
end