package com.parzivail.util.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class FixedResolution extends ScaledResolution
{
	private final int w;
	private final int h;

	public FixedResolution(Minecraft mc, int w, int h)
	{
		super(mc, w, h);
		this.w = w;
		this.h = h;
	}

	@Override
	public double getScaledHeight_double()
	{
		return h;
	}

	@Override
	public double getScaledWidth_double()
	{
		return w;
	}

	@Override
	public int getScaledHeight()
	{
		return h;
	}

	@Override
	public int getScaledWidth()
	{
		return w;
	}

	@Override
	public int getScaleFactor()
	{
		return 1;
	}
}
