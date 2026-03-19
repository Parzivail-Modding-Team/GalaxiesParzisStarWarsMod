package dev.pswg.block.collection;

import dev.pswg.Galaxies;
import dev.pswg.block.RuiningDryingBlock;
import dev.pswg.block.RuiningDryingSlabBlock;
import dev.pswg.block.RuiningDryingStairsBlock;
import dev.pswg.block.VerticalSlabBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReducedDryingRuiningStoneProducts
{
	public final RuiningDryingBlock block;
	public final RuiningDryingStairsBlock stairs;
	public final RuiningDryingSlabBlock slab;

	public ReducedDryingRuiningStoneProducts(BlockBehaviour.Properties settings, String key, Block targetBlock, Block ruinedBlock, int transitionTime, ColorRGBA color)
	{
		this.block = Registrar.block(Galaxies.id(key), blockSettings -> new RuiningDryingBlock(targetBlock, transitionTime, () -> ruinedBlock, blockSettings, color), settings);
		VerticalSlabBlock targetSlab = (VerticalSlabBlock)BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(getBlockKey(targetBlock) + "_slab"));
		VerticalSlabBlock ruinedSlab = (VerticalSlabBlock)BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(getBlockKey(ruinedBlock) + "_slab"));
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), blockSettings -> new RuiningDryingSlabBlock(targetSlab, transitionTime, () -> ruinedSlab, blockSettings), settings);
		StairBlock targetStairs = (StairBlock)BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(getBlockKey(targetBlock) + "_stairs"));
		StairBlock ruinedStairs = (StairBlock)BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(getBlockKey(ruinedBlock) + "_stairs"));
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new RuiningDryingStairsBlock(block.defaultBlockState(), targetStairs, transitionTime, () -> ruinedStairs, blockSettings), settings);
	}

	private static ResourceLocation getBlockKey(Block block)
	{
		return block.builtInRegistryHolder().unwrapKey().get().location();
	}
}