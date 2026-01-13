package dev.pswg.structure;

import dev.pswg.container.GalaxiesBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;

public class StructUtil
{
	public static Block getRandomCrate(Random random)
	{
		int i = random.nextBetween(1, 20);
		return switch (i)
		{
			case 1 -> GalaxiesBlocks.IMPERIAL_CORRUGATED_CRATE;
			case 2 -> GalaxiesBlocks.MEDICAL_CORRUGATED_CRATE;
			case 3 -> GalaxiesBlocks.MINING_CORRUGATED_CRATE;
			case 4 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.BLACK);
			case 5 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.BLUE);
			case 6 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.BROWN);
			case 7 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.CYAN);
			case 8 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.GRAY);
			case 9 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.GREEN);
			case 10 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.LIGHT_BLUE);
			case 11 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.LIGHT_GRAY);
			case 12 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.LIME);
			case 13 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.MAGENTA);
			case 14 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.ORANGE);
			case 15 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.PINK);
			case 16 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.PURPLE);
			case 17 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.RED);
			case 18 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.WHITE);
			case 19 -> GalaxiesBlocks.CORRUGATED_CRATE.get(DyeColor.YELLOW);

			default -> GalaxiesBlocks.IMPERIAL_CORRUGATED_CRATE;
		};
	}
}
