package dev.pswg.block;

import dev.pswg.Galaxies;
import dev.pswg.registry.Registrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.registry.Registries;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;

public class ReducedDryingStoneProducts
{
	public final DryingBlock block;
	public final DryingStairsBlock stairs;
	public final DryingSlabBlock slab;

	public ReducedDryingStoneProducts(AbstractBlock.Settings settings, String key, Block targetBlock, int transitionTime, ColorCode color)
	{
		this.block = Registrar.block(Galaxies.id(key), blockSettings -> new DryingBlock(targetBlock, transitionTime, blockSettings, color), settings);
		VerticalSlabBlock targetSlab = (VerticalSlabBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(targetBlock) + "_slab"));
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), blockSettings -> new DryingSlabBlock(targetSlab, transitionTime, blockSettings), settings);
		StairsBlock targetStairs = (StairsBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(targetBlock) + "_stairs"));
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new DryingStairsBlock(block.getDefaultState(), targetStairs, transitionTime, blockSettings), settings);
	}

	private static Identifier getBlockKey(Block block)
	{
		return block.getRegistryEntry().getKey().get().getValue();
	}
}