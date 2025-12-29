package dev.pswg.structure;

import dev.pswg.container.GadgetsBlocks;
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
			case 1 -> GadgetsBlocks.IMPERIAL_CORRUGATED_CRATE;
			case 2 -> GadgetsBlocks.MEDICAL_CORRUGATED_CRATE;
			case 3 -> GadgetsBlocks.MINING_CORRUGATED_CRATE;
			case 4 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.BLACK);
			case 5 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.BLUE);
			case 6 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.BROWN);
			case 7 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.CYAN);
			case 8 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.GRAY);
			case 9 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.GREEN);
			case 10 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.LIGHT_BLUE);
			case 11 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.LIGHT_GRAY);
			case 12 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.LIME);
			case 13 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.MAGENTA);
			case 14 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.ORANGE);
			case 15 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.PINK);
			case 16 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.PURPLE);
			case 17 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.RED);
			case 18 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.WHITE);
			case 19 -> GadgetsBlocks.CORRUGATED_CRATE.get(DyeColor.YELLOW);

			default -> GadgetsBlocks.IMPERIAL_CORRUGATED_CRATE;
		};
	}
}
