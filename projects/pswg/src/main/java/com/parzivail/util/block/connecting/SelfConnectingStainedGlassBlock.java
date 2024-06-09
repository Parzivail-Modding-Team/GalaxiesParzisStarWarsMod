package com.parzivail.util.block.connecting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;

public class SelfConnectingStainedGlassBlock extends SelfConnectingGlassBlock implements Stainable
{
	private final DyeColor color;
	private static final MapCodec<SelfConnectingStainedGlassBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					createSettingsCodec(),
					DyeColor.CODEC.fieldOf("color").forGetter(SelfConnectingStainedGlassBlock::getColor)
			).apply(instance, SelfConnectingStainedGlassBlock::new));

	public SelfConnectingStainedGlassBlock(Settings settings, DyeColor color)
	{
		super(settings);
		this.color = color;
	}

	@Override
	protected MapCodec<SelfConnectingStainedGlassBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}
