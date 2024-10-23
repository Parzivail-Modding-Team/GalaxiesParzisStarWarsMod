package dev.pswg.mixin;

import dev.pswg.interaction.ILeftClickingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILeftClickingEntity
{
	// TODO: implement remainder of ILeftClickingEntity

	@Override
	public void pswg$stopUsingItemLeft()
	{
		var self = (LivingEntity)(Object)this;

		if (!this.pswg$getLeftActiveItemStack().isEmpty())
		{
			this.pswg$getLeftActiveItemStack().onStoppedUsing(self.getWorld(), self, this.pswg$getItemLeftUseTimeLeft());
			if (this.pswg$getLeftActiveItemStack().isUsedOnRelease())
			{
				this.pswg$tickLeftActiveItemStack();
			}
		}

		this.pswg$clearLeftActiveItem();
	}

	@Override
	public void pswg$clearLeftActiveItem()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.getWorld().isClient)
		{
			boolean wasUsingItem = self.isUsingItem();
			this.pswg$setLeftUsingItemFlag(false);

			if (wasUsingItem)
				self.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
		}

		this.pswg$setLeftActiveItemStack(ItemStack.EMPTY);
		this.pswg$setItemLeftUseTimeLeft(0);
	}
}
