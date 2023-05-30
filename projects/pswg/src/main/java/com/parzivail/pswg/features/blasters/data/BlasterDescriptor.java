package com.parzivail.pswg.features.blasters.data;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

	public boolean pyrotechnics;

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

	/**
	 * Create a new BlasterDescriptor using the default values.
	 *
	 * @param id   The unique ID that this blaster will be referenced with throughout PSWG, and as a part of the item name.
	 * @param type The broad category that best describes the blaster. The value is used internally to determine how many hands are required to hold the blaster, etc.
	 */
	public BlasterDescriptor(Identifier id, BlasterArchetype type)
	{
		this.id = id;
		this.type = type;

		this.sound = id;
	}

	/**
	 * Specifies the blaster's firing sound.
	 *
	 * @param sound The ID of the sound that plays each time the blaster is fired.
	 *
	 * @return this
	 */
	public BlasterDescriptor sound(Identifier sound)
	{
		this.sound = sound;
		return this;
	}

	public BlasterDescriptor usePyrotechnics()
	{
		this.pyrotechnics = true;
		return this;
	}

	public BlasterDescriptor crosshair(int defaultCrosshair)
	{
		this.defaultCrosshair = defaultCrosshair;
		return this;
	}

	/**
	 * Specifies the blaster's available firing modes and behavior regarding water.
	 *
	 * @param firingModes   A list of firing modes that the player can switch this blaster into. The order provided here is the order the player will cycle through them.
	 * @param waterBehavior Determines how the blaster will behave when fired at or while submerged in water.
	 *
	 * @return this
	 */
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

	/**
	 * Specifies the blaster's combat characteristics.
	 *
	 * @param damage        The maximum amount of half-hearts a single blaster bolt can deal to a target. The actual damage value may be lower than this, as described in `damageFalloff`.
	 * @param range         The maximum range of the fired bolts, in blocks.
	 * @param damageFalloff A function that takes in a value $x$ as a fraction $0\lt x\lt 1$ that corresponds to the percentage of `range` the target is away (i.e. $\frac{d_{target}}{range}$) and returns the percentage of the maximum damage as a fraction $0\le x \le 1$ that should be dealt to a target at that range.
	 *
	 * @return this
	 */
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

	public <T> Optional<T> mapWithAttachment(int attachmentBitmask, HashMap<BlasterAttachmentFunction, T> map)
	{
		for (var attachment : attachmentMap.values())
		{
			if (attachment.function == BlasterAttachmentFunction.NONE)
				continue;

			if ((attachment.bit & attachmentBitmask) != 0)
			{
				if (map.containsKey(attachment.function))
					return Optional.of(map.get(attachment.function));
			}
		}

		return Optional.empty();
	}

	public <T> Optional<T> mapWithAttachment(int attachmentBitmask, BlasterAttachmentFunction function, T value)
	{
		for (var attachment : attachmentMap.values())
		{
			if (attachment.function == BlasterAttachmentFunction.NONE)
				continue;

			if ((attachment.bit & attachmentBitmask) != 0)
			{
				if (attachment.function == function)
					return Optional.of(value);
			}
		}

		return Optional.empty();
	}

	public float stackWithAttachment(int attachmentBitmask, HashMap<BlasterAttachmentFunction, Float> map)
	{
		var coefficient = 1f;

		for (var attachment : attachmentMap.values())
		{
			if (attachment.function == BlasterAttachmentFunction.NONE)
				continue;

			if ((attachment.bit & attachmentBitmask) != 0)
			{
				if (map.containsKey(attachment.function))
					coefficient *= map.get(attachment.function);
			}
		}

		return coefficient;
	}

	public float stackWithAttachment(int attachmentBitmask, BlasterAttachmentFunction function, float multiplier)
	{
		var coefficient = 1f;

		for (var attachment : attachmentMap.values())
		{
			if (attachment.function == BlasterAttachmentFunction.NONE)
				continue;

			if ((attachment.bit & attachmentBitmask) != 0)
			{
				if (attachment.function == function)
					coefficient *= multiplier;
			}
		}

		return coefficient;
	}
}
