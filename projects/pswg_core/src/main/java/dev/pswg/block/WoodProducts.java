package dev.pswg.block;

import dev.pswg.Galaxies;
import dev.pswg.registry.Registrar;
import net.minecraft.block.*;

public class WoodProducts
{
	public final Block plank;
	public final StairsBlock stairs;
	public final VerticalSlabBlock slab;
	public final FenceBlock fence;
	public final FenceGateBlock gate;
	public final TrapdoorBlock trapdoor;
	public final DoorBlock door;

	public WoodProducts(String key, AbstractBlock.Settings settings)
	{
		this.plank = Registrar.block(Galaxies.id(key+"_planks"), Block::new, settings);
		this.stairs = Registrar.block(Galaxies.id(key+"_stairs"), blockSettings -> new StairsBlock(plank.getDefaultState(), blockSettings), settings);
		this.slab = Registrar.block(Galaxies.id(key+"_slab"), VerticalSlabBlock::new, settings);
		this.fence = Registrar.block(Galaxies.id(key+"_fence"),FenceBlock::new, settings);
		this.gate = Registrar.block(Galaxies.id(key+"_gate"), blockSettings -> new FenceGateBlock(WoodType.OAK, blockSettings), settings);
		this.trapdoor = Registrar.block(Galaxies.id(key+"_trapdoor"), blockSettings -> new TrapdoorBlock(BlockSetType.OAK, blockSettings), settings);
		this.door = Registrar.block(Galaxies.id(key+"_door"), blockSettings -> new DoorBlock(BlockSetType.OAK, blockSettings),settings);
	}
}