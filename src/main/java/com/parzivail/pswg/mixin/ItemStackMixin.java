package com.parzivail.pswg.mixin;

import com.google.common.collect.Multimap;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ItemStackEntityAttributeModifiers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract void setTag(@Nullable NbtCompound tag);

	@Inject(method = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("TAIL"))
	private void init(ItemConvertible item, int count, CallbackInfo ci)
	{
		if (item instanceof IDefaultNbtProvider)
			setTag(((IDefaultNbtProvider)item).getDefaultTag(item, count));
	}

	@Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
	private void getAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> info)
	{
		if (getItem() instanceof ItemStackEntityAttributeModifiers item)
		{
			info.setReturnValue(item.getAttributeModifiers(equipmentSlot, (ItemStack)(Object)this));
		}
	}
}
