package com.parzivail.pswg.world.feature;

import com.mojang.serialization.Codec;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.world.ProcNoise;
import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MesaMushroomFeature extends Feature<SingleStateFeatureConfig>
{
	public MesaMushroomFeature(Codec<SingleStateFeatureConfig> codec)
	{
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SingleStateFeatureConfig> context)
	{
		var origin = context.getOrigin();
		var structureWorldAccess = context.getWorld();
		var random = context.getRandom();
		var noise = new ProcNoise(random.nextLong());

		SingleStateFeatureConfig singleStateFeatureConfig;
		for (singleStateFeatureConfig = context.getConfig(); origin.getY() > structureWorldAccess.getBottomY() + 3; origin = origin.down())
		{
			if (!structureWorldAccess.isAir(origin.down()))
			{
				var blockState = structureWorldAccess.getBlockState(origin.down());
				if (blockState.isOf(SwgBlocks.Sand.Desert))
					break;
			}
		}

		if (origin.getY() <= structureWorldAccess.getBottomY() + 6)
		{
			return false;
		}
		else
		{
			var height = random.nextInt(50) + 25;
			for (var y = -3; y < height; ++y)
			{
				for (var x = -15; x <= 15; x++)
				{
					for (var z = -15; z <= 15; z++)
					{
						var angle = Math.atan2(z, x);
						var nX = Math.cos(angle);
						var nY = Math.sin(angle);
						var r = 0.3f;
						var oct1 = noise.noise(nX * r, nY * r, y / 40f) * 0.4;
						var oct2 = noise.noise(nX * r * 2, nY * r * 2, y / 20f) * 0.35;
						var oct3 = noise.noise(nX * r * 4, nY * r * 4, y / 10f) * 0.25;
						var radius = (oct1 + oct2 + oct3) * 15;

						radius *= 1 + Math.pow(-y / (float)height, 15);

						if (Math.pow(x, 2) + Math.pow(z, 2) < radius * radius)
							structureWorldAccess.setBlockState(origin.add(x, y, z), singleStateFeatureConfig.state, Block.NO_REDRAW);
					}
				}
			}

			return true;
		}
	}
}
