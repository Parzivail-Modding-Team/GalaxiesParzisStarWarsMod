package dev.pswg.block;

import dev.pswg.Galaxies;
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
		this.block = Registrar.block(Galaxies.id(key), Block::new, settings);
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), VerticalSlabBlock::new, settings);
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new StairsBlock(block.getDefaultState(), blockSettings), settings);
		this.wall = Registrar.block(Galaxies.id(key + "_wall"), WallBlock::new, settings);
	}
}