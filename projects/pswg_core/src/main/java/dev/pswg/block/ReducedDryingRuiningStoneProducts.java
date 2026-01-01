package dev.pswg.block;

import dev.pswg.Galaxies;
import dev.pswg.registry.Registrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.registry.Registries;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;

public class ReducedDryingRuiningStoneProducts
{
	public final RuiningDryingBlock block;
	public final RuiningDryingStairsBlock stairs;
	public final RuiningDryingSlabBlock slab;

	public ReducedDryingRuiningStoneProducts(AbstractBlock.Settings settings, String key, Block targetBlock, Block ruinedBlock, int transitionTime, ColorCode color)
	{
		this.block = Registrar.block(Galaxies.id(key), blockSettings -> new RuiningDryingBlock(targetBlock, transitionTime, () -> ruinedBlock, blockSettings, color), settings);
		VerticalSlabBlock targetSlab = (VerticalSlabBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(targetBlock) + "_slab"));
		VerticalSlabBlock ruinedSlab = (VerticalSlabBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(ruinedBlock) + "_slab"));
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), blockSettings -> new RuiningDryingSlabBlock(targetSlab, transitionTime, () -> ruinedSlab, blockSettings), settings);
		StairsBlock targetStairs = (StairsBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(targetBlock) + "_stairs"));
		StairsBlock ruinedStairs = (StairsBlock)Registries.BLOCK.get(Identifier.of(getBlockKey(ruinedBlock) + "_stairs"));
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new RuiningDryingStairsBlock(block.getDefaultState(), targetStairs, transitionTime, () -> ruinedStairs, blockSettings), settings);
	}

	private static Identifier getBlockKey(Block block)
	{
		return block.getRegistryEntry().getKey().get().getValue();
	}
}