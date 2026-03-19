package dev.pswg.block.collection;

import dev.pswg.Galaxies;
import dev.pswg.block.VerticalSlabBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodProducts
{
	public final Block plank;
	public final StairBlock stairs;
	public final VerticalSlabBlock slab;
	public final FenceBlock fence;
	public final FenceGateBlock gate;
	public final TrapDoorBlock trapdoor;
	public final DoorBlock door;

	public WoodProducts(String key, BlockBehaviour.Properties settings)
	{
		this.plank = Registrar.block(Galaxies.id(key+"_planks"), Block::new, settings);
		this.stairs = Registrar.block(Galaxies.id(key+"_stairs"), blockSettings -> new StairBlock(plank.defaultBlockState(), blockSettings), settings);
		this.slab = Registrar.block(Galaxies.id(key+"_slab"), VerticalSlabBlock::new, settings);
		this.fence = Registrar.block(Galaxies.id(key+"_fence"),FenceBlock::new, settings);
		this.gate = Registrar.block(Galaxies.id(key+"_gate"), blockSettings -> new FenceGateBlock(WoodType.OAK, blockSettings), settings);
		this.trapdoor = Registrar.block(Galaxies.id(key+"_trapdoor"), blockSettings -> new TrapDoorBlock(BlockSetType.OAK, blockSettings), settings);
		this.door = Registrar.block(Galaxies.id(key+"_door"), blockSettings -> new DoorBlock(BlockSetType.OAK, blockSettings),settings);
	}
}
