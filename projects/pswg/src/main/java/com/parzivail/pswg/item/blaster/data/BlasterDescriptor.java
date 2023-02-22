package com.parzivail.pswg.item.blaster.data;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class BlasterDescriptor
{
	public Identifier id;
	public Identifier sound;
	public BlasterArchetype type;
	public List<BlasterFiringMode> firingModes;
	public BlasterWaterBehavior waterBehavior;
	public float damage;
	public Function<Double, Double> damageFalloff;
	public float range;
	public float adsSpeedModifier;
	public float weight;
	public int boltColor;
	public float boltLength;
	public float boltRadius;
	public int magazineSize;

	public int automaticRepeatTime;
	public int burstRepeatTime;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float adsZoom = 5;

	public int burstSize;

	public int burstGap;
	public int quickdrawDelay;

	public int defaultCrosshair;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public int attachmentDefault;
	public int attachmentMinimum;
	public HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap;

	public BlasterDescriptor(Identifier id, Identifier sound, BlasterArchetype type, List<BlasterFiringMode> firingModes, BlasterWaterBehavior waterBehavior, float damage, Function<Double, Double> damageFalloff, float range, float adsSpeedModifier, float weight, int boltColor, float boltLength, float boltRadius, int magazineSize, int automaticRepeatTime, int burstRepeatTime, int burstSize, int burstGap, int quickdrawDelay, int defaultCrosshair, BlasterAxialInfo recoil, BlasterAxialInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling, BlasterAttachmentMap attachmentMap)
	{
		this.id = id;
		this.sound = sound;
		this.type = type;
		this.firingModes = firingModes;
		this.waterBehavior = waterBehavior;
		this.damage = damage;
		this.damageFalloff = damageFalloff;
		this.range = range;
		this.adsSpeedModifier = adsSpeedModifier;
		this.weight = weight;
		this.boltColor = boltColor;
		this.boltLength = boltLength;
		this.boltRadius = boltRadius;
		this.magazineSize = magazineSize;
		this.automaticRepeatTime = automaticRepeatTime;
		this.burstRepeatTime = burstRepeatTime;
		this.burstSize = burstSize;
		this.burstGap = burstGap;
		this.quickdrawDelay = quickdrawDelay;
		this.defaultCrosshair = defaultCrosshair;
		this.recoil = recoil;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
		this.attachmentDefault = attachmentMap.attachmentDefault();
		this.attachmentMinimum = attachmentMap.attachmentMinimum();
		this.attachmentMap = attachmentMap.attachmentMap();
	}
}
