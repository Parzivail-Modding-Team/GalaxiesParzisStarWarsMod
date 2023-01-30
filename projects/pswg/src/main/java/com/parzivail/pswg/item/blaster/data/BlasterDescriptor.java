package com.parzivail.pswg.item.blaster.data;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;

public class BlasterDescriptor
{
	public Identifier id;
	public Identifier sound;
	public BlasterArchetype type;
	public List<BlasterFiringMode> firingModes;
	public BlasterWaterBehavior waterBehavior;
	public float damage;
	public float range;
	public float weight;
	public float boltColor;
	public int magazineSize;

	public int automaticRepeatTime;
	public int burstRepeatTime;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float adsZoom = 5;

	public int burstSize;

	public int quickdrawDelay;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public int attachmentDefault;
	public int attachmentMinimum;
	public HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap;

	public BlasterDescriptor(Identifier id, Identifier sound, BlasterArchetype type, List<BlasterFiringMode> firingModes, BlasterWaterBehavior waterBehavior, float damage, float range, float weight, float boltColor, int magazineSize, int automaticRepeatTime, int burstRepeatTime, int burstSize, int quickdrawDelay, BlasterAxialInfo recoil, BlasterAxialInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling, BlasterAttachmentMap attachmentMap)
	{
		this.id = id;
		this.sound = sound;
		this.type = type;
		this.firingModes = firingModes;
		this.waterBehavior = waterBehavior;
		this.damage = damage;
		this.range = range;
		this.weight = weight;
		this.boltColor = boltColor;
		this.magazineSize = magazineSize;
		this.automaticRepeatTime = automaticRepeatTime;
		this.burstRepeatTime = burstRepeatTime;
		this.burstSize = burstSize;
		this.quickdrawDelay = quickdrawDelay;
		this.recoil = recoil;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
		this.attachmentDefault = attachmentMap.attachmentDefault();
		this.attachmentMinimum = attachmentMap.attachmentMinimum();
		this.attachmentMap = attachmentMap.attachmentMap();
	}
}
