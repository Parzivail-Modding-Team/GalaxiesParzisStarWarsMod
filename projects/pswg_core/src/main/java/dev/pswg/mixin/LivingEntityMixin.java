package dev.pswg.mixin;

import dev.pswg.interaction.ILivingEntityLeftClickSupport;
import dev.pswg.item.ILeftClickUsable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntityLeftClickSupport
{
	@Unique
	private static final int FLAG_LEFT_USING_ITEM = 0b00000001;

	@Unique
	private static final int FLAG_LEFT_ACTIVE_HAND = 0b00000010;

	@Unique
	protected final TrackedData<Byte> LEFT_USING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);

	@Unique
	protected ItemStack activeItemStackLeft;

	@Unique
	protected int itemUseLeftTimeLeft;

	@Shadow
	protected abstract void tickActiveItemStack();

	@Shadow
	protected abstract void tickItemStackUsage(ItemStack stack);

	@Inject(method = "initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V", at = @At("TAIL"))
	public void initDataTracker(DataTracker.Builder builder, CallbackInfo ci)
	{
		builder.add(LEFT_USING_FLAGS, (byte)0);
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V", at = @At("TAIL"))
	public void onTrackedDataSet(TrackedData<?> data, CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		if (LEFT_USING_FLAGS.equals(data) && self.getWorld().isClient)
		{
			if (galaxies$isUsingItemLeft() && activeItemStackLeft.isEmpty())
			{
				activeItemStackLeft = self.getStackInHand(self.getActiveHand());
				if (!activeItemStackLeft.isEmpty())
				{
					itemUseLeftTimeLeft = activeItemStackLeft.getMaxUseTime(self);
				}
			}
			else if (!galaxies$isUsingItemLeft() && !activeItemStackLeft.isEmpty())
			{
				activeItemStackLeft = ItemStack.EMPTY;
				itemUseLeftTimeLeft = 0;
			}
		}
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;setCurrentHand(Lnet/minecraft/util/Hand;)V", at = @At("TAIL"))
	public void setCurrentHand(Hand hand, CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		ItemStack itemStack = self.getStackInHand(hand);
		if (!itemStack.isEmpty() && !galaxies$isUsingItemLeft())
		{
			activeItemStackLeft = itemStack;
			itemUseLeftTimeLeft = itemStack.getMaxUseTime(self);
			if (!self.getWorld().isClient)
			{
				galaxies$setLeftFlag(FLAG_LEFT_USING_ITEM, true);
				galaxies$setLeftFlag(FLAG_LEFT_ACTIVE_HAND, hand == Hand.OFF_HAND);
				self.emitGameEvent(GameEvent.ITEM_INTERACT_START);
			}
		}
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;clearActiveItem()V", at = @At("TAIL"))
	public void clearActiveItem(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		if (!self.getWorld().isClient)
		{
			boolean bl = galaxies$isUsingItemLeft();
			galaxies$setLeftFlag(FLAG_LEFT_USING_ITEM, false);
			if (bl)
			{
				self.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
			}
		}

		activeItemStackLeft = ItemStack.EMPTY;
		itemUseLeftTimeLeft = 0;
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;tickActiveItemStack()V", at = @At("TAIL"))
	private void tickActiveItemStack(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		if (galaxies$isUsingItemLeft())
		{
			if (ItemStack.areItemsEqual(self.getStackInHand(self.getActiveHand()), activeItemStackLeft))
			{
				activeItemStackLeft = self.getStackInHand(self.getActiveHand());
				tickItemStackUsage(activeItemStackLeft);
			}
			else
			{
				self.clearActiveItem();
			}
		}
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;tickItemStackUsage(Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
	protected void tickItemStackUsage(ItemStack stack, CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		stack.usageTick(self.getWorld(), self, galaxies$getItemUseLeftTimeLeft());
		if (--itemUseLeftTimeLeft == 0 && !self.getWorld().isClient && !stack.isUsedOnRelease())
		{
			galaxies$consumeItemLeft();
		}
	}

	@Override
	public void galaxies$setLeftFlag(int mask, boolean value)
	{
		var self = (LivingEntity)(Object)this;

		int flags = self.getDataTracker().get(LEFT_USING_FLAGS);
		if (value)
			flags |= mask;
		else
			flags &= ~mask;

		self.getDataTracker().set(LEFT_USING_FLAGS, (byte)flags);
	}

	@Override
	public void galaxies$stopUsingItemLeft()
	{
		var self = (LivingEntity)(Object)this;

		if (!activeItemStackLeft.isEmpty())
		{
			((ILeftClickUsable)activeItemStackLeft.getItem()).onStoppedUsingLeft(activeItemStackLeft, self.getWorld(), self, galaxies$getItemUseLeftTimeLeft());

			if (((ILeftClickUsable)activeItemStackLeft.getItem()).isUsedOnLeftRelease(activeItemStackLeft))
			{
				tickActiveItemStack();
			}
		}

		self.clearActiveItem();
	}

	@Override
	public int galaxies$getItemUseLeftTimeLeft()
	{
		return this.itemUseLeftTimeLeft;
	}

	@Override
	public void galaxies$consumeItemLeft()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.getWorld().isClient || self.isUsingItem())
		{
			Hand hand = self.getActiveHand();
			if (!activeItemStackLeft.equals(self.getStackInHand(hand)))
			{
				self.stopUsingItem();
			}
			else
			{
				if (!activeItemStackLeft.isEmpty() && galaxies$isUsingItemLeft())
				{
					((ILeftClickUsable)activeItemStackLeft.getItem()).finishUsingLeft(activeItemStackLeft, self.getWorld(), self);
					self.clearActiveItem();
				}
			}
		}
	}
}
