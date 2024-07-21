package com.parzivail.pswg.features.blasters.data;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class BlasterDescriptor
{
	public final Identifier id;

	public Identifier sound;
	public BlasterArchetype type;
	public List<BlasterFiringMode> firingModes;
	public BlasterWaterBehavior waterBehavior;
	public float damage;
	public DoubleUnaryOperator damageFalloff;
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

	/**
	 * Specifies that the blaster should emit smoke and spark particles from the barrel
	 * when fired. Emulates the pyrotechnics used on the Jawa ionization blaster in
	 * A New Hope.
	 *
	 * @return this
	 */
	public BlasterDescriptor usePyrotechnics()
	{
		this.pyrotechnics = true;
		return this;
	}

	/**
	 * Specifies the blaster's default HUD crosshair.
	 *
	 * @param defaultCrosshair The zero-based index into the HUD crosshair texture table.
	 *
	 * @return this
	 */
	public BlasterDescriptor crosshair(int defaultCrosshair)
	{
		this.defaultCrosshair = defaultCrosshair;
		return this;
	}

	/**
	 * Specifies the blaster's available firing modes and behavior regarding water.
	 *
	 * @param firingModes   A list of firing modes that the player can switch this blaster into. The
	 *                      order provided here is the order the player will cycle through them.
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

	/**
	 * Specifies properties related to the blaster's physical attributes.
	 *
	 * @param weight           The weight of the blaster, in units (reserved for future use).
	 * @param adsSpeedModifier The player speed attribute coefficient used to slow down the player. When
	 *                         ADS, the player's speed is multiplied by this value to determine the new
	 *                         speed of the player with the equation $AdsSpeed_{Player} = adsSpeedModifier \cdot BaseSpeed_{Player}$.
	 * @param quickdrawDelay   The delay, in ticks, it takes for this blaster to become "ready" after
	 *                         scrolling to it in your hotbar.
	 * @param magazineSize     The size of the blaster's internal "magazine" of rounds. This is the maximum number
	 *                         of shots the blaster will attempt to draw from power packs in the inventory when
	 *                         the internal magazine is empty.
	 *
	 * @return this
	 */
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
	 * @param damage        The maximum amount of half-hearts a single blaster bolt can deal to a target. The
	 *                      actual damage value may be lower than this, as described in `damageFalloff`.
	 * @param range         The maximum range of the fired bolts, in blocks.
	 * @param damageFalloff A function that takes in a value $x$ as a fraction $0\lt x\lt 1$ that corresponds
	 *                      to the percentage of `range` the target is away (i.e. $\frac{d_{target}}{range}$)
	 *                      and returns the percentage of the maximum damage as a fraction $0\le x \le 1$ that
	 *                      should be dealt to a target at that range.
	 *
	 * @return this
	 */
	public BlasterDescriptor damage(float damage, float range, DoubleUnaryOperator damageFalloff)
	{
		this.damage = damage;
		this.range = range;
		this.damageFalloff = damageFalloff;
		return this;
	}

	/**
	 * Specifies the blaster's bolt presentation.
	 *
	 * @param boltColor  The color of the bolt, as a packed HSV integer (See `com.parzivail.util.math.ColorUtil::packHsv`).
	 * @param boltLength The length coefficient of the blaster bolt relative to a standard bolt.
	 * @param boltRadius The radius coefficient of the blaster bolt relative to a standard bolt.
	 *
	 * @return this
	 */
	public BlasterDescriptor bolt(int boltColor, float boltLength, float boltRadius)
	{
		this.boltColor = boltColor;
		this.boltLength = boltLength;
		this.boltRadius = boltRadius;
		return this;
	}

	/**
	 * Specifies the blaster's parameters when automatic-firing. Only required if "automatic" is
	 * a valid firing mode for this blaster.
	 *
	 * @param automaticRepeatTime The time, in ticks, between two automatic rounds being fired.
	 *
	 * @return this
	 */
	public BlasterDescriptor autoParameters(int automaticRepeatTime)
	{
		this.automaticRepeatTime = automaticRepeatTime;
		return this;
	}

	/**
	 * Specifies the blaster's parameters when burst-firing. Only required if "burst" is
	 * a valid firing mode for this blaster.
	 *
	 * @param burstRepeatTime The time, in ticks, between two burst rounds being fired.
	 * @param burstSize       The size of a burst, in rounds.
	 * @param burstGap        The minimum time, in ticks, between two successive bursts.
	 *
	 * @return this
	 */
	public BlasterDescriptor burstParameters(int burstRepeatTime, int burstSize, int burstGap)
	{
		this.burstRepeatTime = burstRepeatTime;
		this.burstSize = burstSize;
		this.burstGap = burstGap;
		return this;
	}

	/**
	 * Specifies the blaster's recoiling effects on the player who fired it.
	 *
	 * @param recoil The maximum extent of recoil in the axial directions, in degrees.
	 *
	 * @return this
	 */
	public BlasterDescriptor recoil(BlasterAxialInfo recoil)
	{
		this.recoil = recoil;
		return this;
	}

	/**
	 * Specifies the blaster's bolt spreading inaccuracy.
	 *
	 * @param spread The maximum extent of bolt spreading in the axial directions, in degrees.
	 *
	 * @return this
	 */
	public BlasterDescriptor spread(BlasterAxialInfo spread)
	{
		this.spread = spread;
		return this;
	}

	/**
	 * Specifies the blaster's heating characteristics.
	 *
	 * @param heat An instance of a heating characteristics object.
	 *
	 * @return this
	 */
	public BlasterDescriptor heat(BlasterHeatInfo heat)
	{
		this.heat = heat;
		return this;
	}

	/**
	 * Specifies the blaster's cooling "minigame" parameters.
	 *
	 * @param cooling An instance of a cooling characteristics object.
	 *
	 * @return this
	 */
	public BlasterDescriptor cooling(BlasterCoolingBypassProfile cooling)
	{
		this.cooling = cooling;
		return this;
	}

	/**
	 * Specifies this blaster's attachments.
	 *
	 * @param b A consumer for an attachment builder instance to append attachments to.
	 *
	 * @return this
	 */
	public BlasterDescriptor attachments(Consumer<BlasterAttachmentBuilder> b)
	{
		b.accept(attachmentBuilder);
		return this;
	}

	/**
	 * Freezes this builder and considers it built. Should not be called by addons -- it is
	 * called automatically during mod initialization.
	 */
	@ApiStatus.Internal
	public void build()
	{
		var map = attachmentBuilder.build();

		this.attachmentDefault = map.attachmentDefault();
		this.attachmentMinimum = map.attachmentMinimum();
		this.attachmentMap = map.attachmentMap();

		this.attachmentBuilder = null;
	}

	/**
	 * Given a set of attachments, selects a value from a map that correlates a set of functions to a
	 * set of related values.
	 *
	 * @param attachmentBitmask The populated attachment values of a blaster.
	 * @param map               The map used to find the appropriate return value from the present attachments.
	 * @param <T>               The generic type, inferred from the map value type, to return.
	 *
	 * @return An optional value, if any, containing the first matching value from the map.
	 */
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

	/**
	 * Given an attachment function and related value, determine if the given blaster meets the requirements
	 * to provide that value.
	 *
	 * @param attachmentBitmask The populated attachment values of a blaster.
	 * @param function          The attachment function one of the blaster's attachments must have to provide the value.
	 * @param value             The value to provide should the requisite attachment be present.
	 * @param <T>               The generic type, inferred from the value type, to return.
	 *
	 * @return An optional value, if any, containing the matching value.
	 */
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

	/**
	 * Combines a set of stacking coefficients based on the present attachments of the given blaster.
	 *
	 * @param attachmentBitmask The populated attachment values of a blaster.
	 * @param map               The map used to correlate attachment function with coefficient value.
	 *
	 * @return The coefficient generated by the equation: $presentValueCoefficient_{1} \cdot presentValueCoefficient_{2} \cdot \ldots \cdot presentValueCoefficient_{n}$
	 */
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
}
