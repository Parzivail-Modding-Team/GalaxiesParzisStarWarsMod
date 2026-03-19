package dev.pswg.block.collection;

import dev.pswg.Galaxies;
import dev.pswg.block.VerticalSlabBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StoneProducts
{
	public final Block block;
	public final StairBlock stairs;
	public final VerticalSlabBlock slab;
	public final WallBlock wall;

	public StoneProducts(BlockBehaviour.Properties settings, String key)
	{
		this.block = Registrar.block(Galaxies.id(key), Block::new, settings);
		this.slab = Registrar.block(Galaxies.id(key + "_slab"), VerticalSlabBlock::new, settings);
		this.stairs = Registrar.block(Galaxies.id(key + "_stairs"), blockSettings -> new StairBlock(block.defaultBlockState(), blockSettings), settings);
		this.wall = Registrar.block(Galaxies.id(key + "_wall"), WallBlock::new, settings);
	}
}