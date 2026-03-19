package dev.pswg.block.collection;

import dev.pswg.Galaxies;
import dev.pswg.block.DryingBlock;
import dev.pswg.block.DryingSlabBlock;
import dev.pswg.block.DryingStairsBlock;
import dev.pswg.block.VerticalSlabBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReducedDryingStoneProducts
{
	public final DryingBlock block;
	public final DryingStairsBlock stairs;
	public final DryingSlabBlock slab;

	public ReducedDryingStoneProducts(BlockBehaviour.Properties settings, String key, Block targetBlock, int transitionTime, ColorRGBA color)
	{
		this.block = Registrar.block(Galaxies.id(key), blockSettings -> new DryingBlock(targetBlock, transitionTime, blockSettings, color), settings);
		VerticalSlabBlock targetSlab = (VerticalSlabBlock)BuiltInRegistries.BLOCK.getValue(Identifier.parse(getBlockKey(targetBlock) + "_slab"));
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), blockSettings -> new DryingSlabBlock(targetSlab, transitionTime, blockSettings), settings);
		StairBlock targetStairs = (StairBlock)BuiltInRegistries.BLOCK.getValue(Identifier.parse(getBlockKey(targetBlock) + "_stairs"));
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new DryingStairsBlock(block.defaultBlockState(), targetStairs, transitionTime, blockSettings), settings);
	}

	private static Identifier getBlockKey(Block block)
	{
		return block.builtInRegistryHolder().unwrapKey().get().identifier();
	}
}
