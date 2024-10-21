package dev.pswg.attributes;

import com.google.common.collect.ImmutableList;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * A collection of utilities related to entity attributes
 */
public final class AttributeUtil
{
	/**
	 * Removes a given attribute modifier from the given attribute modifiers component
	 *
	 * @param base      The component from which the modifier will be removed
	 * @param attribute The attribute from which the modifier will be removed
	 * @param modifier  The modifier that will be removed
	 *
	 * @return The component without the given modifier
	 */
	public static AttributeModifiersComponent without(AttributeModifiersComponent base, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier)
	{
		ImmutableList.Builder<AttributeModifiersComponent.Entry> builder = ImmutableList.builderWithExpectedSize(base.modifiers().size() + 1);

		for (AttributeModifiersComponent.Entry entry : base.modifiers())
		{
			if (!entry.matches(attribute, modifier.id()))
			{
				builder.add(entry);
			}
		}

		return new AttributeModifiersComponent(builder.build(), base.showInTooltip());
	}
}
