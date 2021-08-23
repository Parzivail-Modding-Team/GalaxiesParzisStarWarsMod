package com.parzivail.pswg.item.blaster.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class BlasterDescriptor extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("blaster_model");

	public Identifier id;
	public BlasterArchetype type;
	public ArrayList<BlasterFiringMode> firingModes;
	public float damage;
	public float range;
	public float weight;
	public float boltColor;
	public int magazineSize;

	public int automaticRepeatTime;
	public int burstRepeatTime;

	public Vec3d foreGripPos;
	public EulerAngle foreGripHandAngle;

	public int burstSize;

	public BlasterSpreadInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public BlasterDescriptor(NbtCompound tag)
	{
		super(SLUG, tag);
	}

	public BlasterDescriptor(Identifier id, BlasterArchetype type, ArrayList<BlasterFiringMode> firingModes, float damage, float range, float weight, float boltColor, int magazineSize, int automaticRepeatTime, int burstRepeatTime, Vec3d foreGripPos, EulerAngle foreGripHandAngle, int burstSize, BlasterSpreadInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling)
	{
		super(SLUG);
		this.id = id;
		this.type = type;
		this.firingModes = firingModes;
		this.damage = damage;
		this.range = range;
		this.weight = weight;
		this.boltColor = boltColor;
		this.magazineSize = magazineSize;
		this.automaticRepeatTime = automaticRepeatTime;
		this.burstRepeatTime = burstRepeatTime;
		this.foreGripPos = foreGripPos;
		this.foreGripHandAngle = foreGripHandAngle;
		this.burstSize = burstSize;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
	}

	/**
	 * Do not call: only used in deserialization
	 */
	@Deprecated
	public BlasterDescriptor(Identifier id, BlasterDescriptor other)
	{
		super(SLUG);
		this.id = id;
		this.type = other.type;
		this.firingModes = other.firingModes;
		this.damage = other.damage;
		this.range = other.range;
		this.weight = other.weight;
		this.boltColor = other.boltColor;
		this.magazineSize = other.magazineSize;
		this.automaticRepeatTime = other.automaticRepeatTime;
		this.burstRepeatTime = other.burstRepeatTime;
		this.foreGripPos = other.foreGripPos;
		this.foreGripHandAngle = other.foreGripHandAngle;
		this.burstSize = other.burstSize;
		this.spread = other.spread;
		this.heat = other.heat;
		this.cooling = other.cooling;
	}
}
