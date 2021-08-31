package com.parzivail.pswg.mixin;

import com.parzivail.util.item.IItemEntityConsumer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
	@Inject(method = "setStack(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
	private void setStack(ItemStack stack, CallbackInfo ci)
	{
		if (stack.getItem() instanceof IItemEntityConsumer)
			((IItemEntityConsumer)stack.getItem()).onItemEntityCreated((ItemEntity)(Object)this, stack);
	}
}
