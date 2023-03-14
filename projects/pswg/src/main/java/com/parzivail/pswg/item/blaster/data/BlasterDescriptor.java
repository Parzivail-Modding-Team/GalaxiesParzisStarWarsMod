package com.parzivail.pswg.item.blaster.data;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BlasterDescriptor
{
	public final Identifier id;

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

	public int automaticRepeatTime = 1;
	public int burstRepeatTime = 1;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float baseZoom = 5;

	public int burstSize;

	public int burstGap = 1;
	public int quickdrawDelay = 1;

	public int defaultCrosshair;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public int attachmentDefault;
	public int attachmentMinimum;
	public HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap = new HashMap<>();

	private BlasterAttachmentBuilder attachmentBuilder = new BlasterAttachmentBuilder();

	public BlasterDescriptor(Identifier id, BlasterArchetype type)
	{
		this.id = id;
		this.type = type;

		this.sound = id;
	}

	public BlasterDescriptor sound(Identifier sound)
	{
		this.sound = sound;
		return this;
	}

	public BlasterDescriptor crosshair(int defaultCrosshair)
	{
		this.defaultCrosshair = defaultCrosshair;
		return this;
	}

	public BlasterDescriptor firingBehavior(List<BlasterFiringMode> firingModes, BlasterWaterBehavior waterBehavior)
	{
		this.firingModes = firingModes;
		this.waterBehavior = waterBehavior;
		return this;
	}

	public BlasterDescriptor mechanicalProperties(float weight, float adsSpeedModifier, int quickdrawDelay, int magazineSize)
	{
		this.adsSpeedModifier = adsSpeedModifier;
		this.weight = weight;
		this.quickdrawDelay = quickdrawDelay;
		this.magazineSize = magazineSize;
		return this;
	}

	public BlasterDescriptor damage(float damage, float range, Function<Double, Double> damageFalloff)
	{
		this.damage = damage;
		this.range = range;
		this.damageFalloff = damageFalloff;
		return this;
	}

	public BlasterDescriptor bolt(int boltColor, float boltLength, float boltRadius)
	{
		this.boltColor = boltColor;
		this.boltLength = boltLength;
		this.boltRadius = boltRadius;
		return this;
	}

	public BlasterDescriptor autoParameters(int automaticRepeatTime)
	{
		this.automaticRepeatTime = automaticRepeatTime;
		return this;
	}

	public BlasterDescriptor burstParameters(int burstRepeatTime, int burstSize, int burstGap)
	{
		this.burstRepeatTime = burstRepeatTime;
		this.burstSize = burstSize;
		this.burstGap = burstGap;
		return this;
	}

	public BlasterDescriptor recoil(BlasterAxialInfo recoil)
	{
		this.recoil = recoil;
		return this;
	}

	public BlasterDescriptor spread(BlasterAxialInfo spread)
	{
		this.spread = spread;
		return this;
	}

	public BlasterDescriptor heat(BlasterHeatInfo heat)
	{
		this.heat = heat;
		return this;
	}

	public BlasterDescriptor cooling(BlasterCoolingBypassProfile cooling)
	{
		this.cooling = cooling;
		return this;
	}

	public BlasterDescriptor attachments(Consumer<BlasterAttachmentBuilder> b)
	{
		b.accept(attachmentBuilder);
		return this;
	}

	public void build()
	{
		var map = attachmentBuilder.build();

		this.attachmentDefault = map.attachmentDefault();
		this.attachmentMinimum = map.attachmentMinimum();
		this.attachmentMap = map.attachmentMap();

		this.attachmentBuilder = null;
	}
}
