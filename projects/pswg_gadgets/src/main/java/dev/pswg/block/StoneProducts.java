package dev.pswg.block;

import dev.pswg.Gadgets;
import dev.pswg.registry.Registrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;

public class StoneProducts
{
	public final Block block;
	public final StairsBlock stairs;
	public final VerticalSlabBlock slab;
	public final WallBlock wall;

	public StoneProducts(AbstractBlock.Settings settings, String key)
	{
		this.block = Registrar.block(Gadgets.id(key), Block::new, settings);
		this.slab = Registrar.block(Gadgets.id(key + "_slab"), VerticalSlabBlock::new, AbstractBlock.Settings.copy(block));
		this.stairs = Registrar.block(Gadgets.id(key + "_stairs"), blockSettings -> new StairsBlock(block.getDefaultState(), blockSettings), settings);
		this.wall = Registrar.block(Gadgets.id(key + "_wall"), WallBlock::new, settings);
		//this.stairs = new StairsBlock(block.getDefaultState(), AbstractBlock.Settings.copy(block));
		//this.slab = new VerticalSlabBlock(AbstractBlock.Settings.copy(block));
		//this.wall = new WallBlock(AbstractBlock.Settings.copy(block));
	}
}