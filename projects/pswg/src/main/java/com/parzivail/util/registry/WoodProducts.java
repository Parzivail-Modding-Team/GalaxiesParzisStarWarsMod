package com.parzivail.util.registry;

import com.parzivail.util.block.PStairsBlock;
import com.parzivail.util.block.VerticalSlabBlock;
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

	public WoodProducts(Block plank)
	{
		this.plank = plank;
		this.stairs = new PStairsBlock(plank.getDefaultState(), AbstractBlock.Settings.copy(plank));
		this.slab = new VerticalSlabBlock(AbstractBlock.Settings.copy(plank));
		this.fence = new FenceBlock(AbstractBlock.Settings.copy(plank));
		this.gate = new FenceGateBlock(AbstractBlock.Settings.copy(plank));
		this.trapdoor = new TrapdoorBlock(AbstractBlock.Settings.copy(plank));
		this.door = new DoorBlock(AbstractBlock.Settings.copy(plank));
	}
}
