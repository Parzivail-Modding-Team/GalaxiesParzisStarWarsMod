package dev.pswg.attributes;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

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
	public static ItemAttributeModifiers without(ItemAttributeModifiers base, Holder<Attribute> attribute, AttributeModifier modifier)
	{
		ImmutableList.Builder<ItemAttributeModifiers.Entry> builder = ImmutableList.builderWithExpectedSize(base.modifiers().size() + 1);

		for (ItemAttributeModifiers.Entry entry : base.modifiers())
		{
			if (!entry.matches(attribute, modifier.id()))
			{
				builder.add(entry);
			}
		}

		return new ItemAttributeModifiers(builder.build());
	}
}
