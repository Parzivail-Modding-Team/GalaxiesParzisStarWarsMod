package com.parzivail.util.item;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

public interface ItemStackEntityAttributeModifiers
{
	ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack);
}
