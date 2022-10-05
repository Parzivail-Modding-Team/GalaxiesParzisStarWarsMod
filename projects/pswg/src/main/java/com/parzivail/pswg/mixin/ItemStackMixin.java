package com.parzivail.pswg.mixin;

import com.parzivail.util.item.IDefaultNbtProvider;
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

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract void setNbt(@Nullable NbtCompound tag);

	@Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("TAIL"))
	private void init(ItemConvertible item, int count, CallbackInfo ci)
	{
		if (item instanceof IDefaultNbtProvider)
			setNbt(((IDefaultNbtProvider)item).getDefaultTag(item, count));
	}
}
