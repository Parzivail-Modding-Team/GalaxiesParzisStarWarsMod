package com.parzivail.pswg.mixin;

import com.parzivail.util.item.IItemEntityStackSetListener;
import com.parzivail.util.item.IItemEntityTickListener;
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
		if (stack.getItem() instanceof IItemEntityStackSetListener iecl)
			iecl.onItemEntityStackSet((ItemEntity)(Object)this, stack);
	}

	@Inject(method = "tick()V", at = @At("HEAD"))
	private void tick(CallbackInfo ci)
	{
		var entity = (ItemEntity)(Object)this;
		var stack = entity.getStack();

		if (stack.getItem() instanceof IItemEntityTickListener ietl && ietl.onItemEntityTick(entity, stack))
			entity.setStack(stack);
	}
}
