package com.parzivail.pswg.item.blaster;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class BlasterDescriptor extends TagSerializer
{
	public static final Identifier SLUG = Resources.identifier("blaster_model");

	public Identifier id;
	public float damage;
	public float range;
	public float weight;
	public int boltColor;
	public int magazineSize;
	public int automaticRepeatTime;

	public BlasterSpreadInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public BlasterDescriptor(CompoundTag tag)
	{
		super(SLUG, tag);
	}

	public BlasterDescriptor(Identifier id, float damage, float range, float weight, int boltColor, int magazineSize, int automaticRepeatTime, BlasterSpreadInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling)
	{
		super(SLUG);
		this.id = id;
		this.damage = damage;
		this.range = range;
		this.weight = weight;
		this.boltColor = boltColor;
		this.magazineSize = magazineSize;
		this.automaticRepeatTime = automaticRepeatTime;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
	}
}
