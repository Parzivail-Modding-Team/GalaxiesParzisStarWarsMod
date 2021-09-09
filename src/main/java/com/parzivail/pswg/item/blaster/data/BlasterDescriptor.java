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

	public byte automaticRepeatTime;
	public byte burstRepeatTime;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float adsZoom = 5;

	public Vec3d muzzlePos;
	public Vec3d foreGripPos;
	public EulerAngle foreGripHandAngle;

	public byte burstSize;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public BlasterDescriptor(NbtCompound tag)
	{
		super(SLUG, tag);
	}

	public BlasterDescriptor(Identifier id, BlasterArchetype type, ArrayList<BlasterFiringMode> firingModes, float damage, float range, float weight, float boltColor, int magazineSize, byte automaticRepeatTime, byte burstRepeatTime, Vec3d muzzlePos, Vec3d foreGripPos, EulerAngle foreGripHandAngle, byte burstSize, BlasterAxialInfo recoil, BlasterAxialInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling)
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
		this.muzzlePos = muzzlePos;
		this.foreGripPos = foreGripPos;
		this.foreGripHandAngle = foreGripHandAngle;
		this.burstSize = burstSize;
		this.recoil = recoil;
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
		this.muzzlePos = other.muzzlePos;
		this.foreGripPos = other.foreGripPos;
		this.foreGripHandAngle = other.foreGripHandAngle;
		this.burstSize = other.burstSize;
		this.recoil = other.recoil;
		this.spread = other.spread;
		this.heat = other.heat;
		this.cooling = other.cooling;
	}
}
