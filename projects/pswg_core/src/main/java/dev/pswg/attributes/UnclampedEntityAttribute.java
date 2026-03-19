package dev.pswg.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Represents an attribute for an entity that is not clamped
 * to upper and lower bounds
 */
public class UnclampedEntityAttribute extends Attribute
{
	protected UnclampedEntityAttribute(String translationKey, double fallback)
	{
		super(translationKey, fallback);
	}
}
