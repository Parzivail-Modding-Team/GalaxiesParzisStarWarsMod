package com.parzivail.pswg.item.blaster.data;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class BlasterDescriptor
{
	public Identifier id;
	public BlasterArchetype type;
	public ArrayList<BlasterFiringMode> firingModes;
	public float damage;
	public float range;
	public float weight;
	public float boltColor;
	public int magazineSize;

	public short automaticRepeatTime;
	public short burstRepeatTime;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float adsZoom = 5;

	public short burstSize;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public int attachmentDefault;
	public int attachmentMinimum;
	public HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap;

	public BlasterDescriptor(Identifier id, BlasterArchetype type, ArrayList<BlasterFiringMode> firingModes, float damage, float range, float weight, float boltColor, int magazineSize, short automaticRepeatTime, short burstRepeatTime, short burstSize, BlasterAxialInfo recoil, BlasterAxialInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling, int attachmentDefault, int attachmentMinimum, HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap)
	{
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
		this.burstSize = burstSize;
		this.recoil = recoil;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
		this.attachmentDefault = attachmentDefault;
		this.attachmentMinimum = attachmentMinimum;
		this.attachmentMap = attachmentMap;
	}
}
