package dev.pswg.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;

public class SelfConnectingStainedGlassBlock extends SelfConnectingGlassBlock implements BeaconBeamBlock
{
	private final DyeColor color;

	public SelfConnectingStainedGlassBlock(DyeColor color, Properties settings)
	{
		super(settings);
		this.color = color;
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}