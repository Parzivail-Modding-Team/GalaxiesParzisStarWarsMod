package dev.pswg.block;

import dev.pswg.Gadgets;
import dev.pswg.registry.Registrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;

public class ReducedStoneProducts
{
	public final Block block;
	public final StairsBlock stairs;
	public final VerticalSlabBlock slab;

	public ReducedStoneProducts(AbstractBlock.Settings settings, String key)
	{
		this.block = Registrar.block(Gadgets.id(key), Block::new, settings);
		this.slab = Registrar.block(Gadgets.id(key + "_slab"), VerticalSlabBlock::new, settings);
		this.stairs = Registrar.block(Gadgets.id(key + "_stairs"), blockSettings -> new StairsBlock(block.getDefaultState(), blockSettings), settings);
	}
}