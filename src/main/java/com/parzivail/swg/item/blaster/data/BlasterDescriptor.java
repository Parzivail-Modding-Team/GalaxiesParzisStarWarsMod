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
	public final int clipSize;
	public final int roundsBeforeOverheat;
	public final int cooldownTimeTicks;

	public BlasterDescriptor(String name, float damage, float spread, int range, int cost, float weight, int clipSize, int boltColor, int roundsBeforeOverheat, int cooldownTimeTicks)
	{
		this.name = name;
		this.damage = damage;
		this.spread = spread;
		this.range = range;
		this.cost = cost;
		this.weight = weight;
		this.clipSize = clipSize;
		this.boltColor = boltColor;
		this.roundsBeforeOverheat = roundsBeforeOverheat;
		this.cooldownTimeTicks = cooldownTimeTicks;
	}
}
