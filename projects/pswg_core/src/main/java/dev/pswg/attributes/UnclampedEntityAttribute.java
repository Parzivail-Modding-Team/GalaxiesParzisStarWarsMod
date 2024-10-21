package dev.pswg.attributes;

import net.minecraft.entity.attribute.EntityAttribute;

/**
 * Represents an attribute for an entity that is not clamped
 * to upper and lower bounds
 */
public class UnclampedEntityAttribute extends EntityAttribute
{
	protected UnclampedEntityAttribute(String translationKey, double fallback)
	{
		super(translationKey, fallback);
	}
}
