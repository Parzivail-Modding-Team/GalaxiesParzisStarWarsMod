-- Controls the Y at which water starts
waterLevel = 0

function clamp(x)
	if (x > 1) then
		return 1
	end
	if (x < 0) then
		return 0
	end
	return x
end

--[[
float swissTurbulence(float2 p, float seed, int octaves,
                      float lacunarity = 2.0, float gain = 0.5,
                      float warp = 0.15>)
					  
float sum = 0;
float freq = 1.0, amp = 1.0;
float2 dsum = float2(0,0);
for(int i=0; i < octaves; i++)
{
	float3 n = perlinNoiseDeriv((p + warp * dsum)*freq, seed + i);
	sum += amp * (1 - abs(n.x));
	dsum += amp * n.yz * -n.x;
	freq *= lacunarity;
	amp *= gain * saturate(sum);
}
return sum;
]]--

function swissTurbulence(pX, pY, octaves, lacunarity, gain, warp)
    local sum = 0
	local freq = 1
	local amp = 1
	local dSumX = 0
	local dSumY = 0
	
	for i = 0, octaves do
		nX = noise((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000)
		nY = noiseDx((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000)
		nZ = noiseDz((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000)
		
		sum = sum + amp * (1 - math.abs(nX))
		dSumX = dSumX + amp * nY * -nX
		dSumY = dSumY + amp * nZ * -nX
		freq = freq * lacunarity
		amp = amp * gain * clamp(sum)
	end
	
	return sum
end

-- This is the main method that gets called to
-- find the height of the terrain at each point.
function terrain(x, z)
	return swissTurbulence(x / 300, z / 300, 4, 3, 0.5, 0.25) * 150
end

function tree(x, y, z)
	return TREE_NONE
end