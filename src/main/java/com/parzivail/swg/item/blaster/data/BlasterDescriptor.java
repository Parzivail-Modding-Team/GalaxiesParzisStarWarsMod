package com.parzivail.swg.item.blaster.data;

public class BlasterDescriptor
{
	public final String name;
	public final float damage;
	public final float spread;
	public final int range;
	public final int cost;
	public final float weight;
	public final int boltColor;
	private final int clipSize;

	public BlasterDescriptor(String name, float damage, float spread, int range, int cost, float weight, int clipSize, int boltColor)
	{
		this.name = name;
		this.damage = damage;
		this.spread = spread;
		this.range = range;
		this.cost = cost;
		this.weight = weight;
		this.clipSize = clipSize;
		this.boltColor = boltColor;
	}
}
