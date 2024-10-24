package dev.pswg.mixin.leftuse;

import dev.pswg.Galaxies;
import dev.pswg.interaction.ILeftClickingEntity;
import dev.pswg.interaction.LeftClickingEntityAttachment;
import dev.pswg.item.ILeftClickUsable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Main featureset for left-use support in entities. Handles most
 * item interactions and data storage. Abstracts out some calls
 * to {@link ItemStack} with calls to the item itself, where
 * applicable.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILeftClickingEntity
{
	@Unique
	private int itemLeftUseTimeLeft;

	@Unique
	private ItemStack leftActiveItemStack;

	@Override
	public boolean pswg$isLeftUsingItem()
	{
		var self = (LivingEntity)(Object)this;

		var attachment = LeftClickingEntityAttachment.get(self);
		return attachment.isUsingItemLeft();
	}

	@Override
	public void pswg$setLeftUsingItem(boolean isLeftUsing)
	{
		var self = (LivingEntity)(Object)this;

		var attachment = LeftClickingEntityAttachment.get(self);
		self.setAttached(Galaxies.LEFT_CLICKING_ATTACHMENT, attachment.withIsLeftUsing(isLeftUsing));

		if (this.pswg$isLeftUsingItem() && this.pswg$getLeftActiveItemStack().isEmpty())
		{
			var activeStack = self.getStackInHand(self.getActiveHand());
			this.pswg$setLeftActiveItemStack(activeStack);

			if (!(activeStack.getItem() instanceof ILeftClickUsable leftClickingItem))
				throw new RuntimeException("Attempted to set using non-left-clicking item");

			if (!this.pswg$getLeftActiveItemStack().isEmpty())
			{
				this.pswg$setItemLeftUseTimeLeft(leftClickingItem.getMaxUseLeftTime(activeStack, self));
			}
		}
		else if (!this.pswg$isLeftUsingItem() && !this.pswg$getLeftActiveItemStack().isEmpty())
		{
			this.pswg$setLeftActiveItemStack(ItemStack.EMPTY);
			this.pswg$setItemLeftUseTimeLeft(0);
		}
	}

	@Override
	public int pswg$getItemLeftUseTimeLeft()
	{
		return itemLeftUseTimeLeft;
	}

	@Override
	public void pswg$setItemLeftUseTimeLeft(int timeLeft)
	{
		// TODO: find all setters
		itemLeftUseTimeLeft = timeLeft;
	}

	@Override
	public ItemStack pswg$getLeftActiveItemStack()
	{
		return leftActiveItemStack;
	}

	@Override
	public void pswg$setLeftActiveItemStack(ItemStack stack)
	{
		// TODO: find all setters
		leftActiveItemStack = stack;
	}

	@Override
	public void pswg$stopLeftUsingItem()
	{
		var self = (LivingEntity)(Object)this;

		if (!this.pswg$getLeftActiveItemStack().isEmpty())
		{
			var activeStack = this.pswg$getLeftActiveItemStack();

			if (!(activeStack.getItem() instanceof ILeftClickUsable leftClickingItem))
				throw new RuntimeException("Attempted to stop using non-left-clicking item");

			leftClickingItem.onStoppedUsingLeft(activeStack, self.getWorld(), self, this.pswg$getItemLeftUseTimeLeft());
			if (leftClickingItem.isUsedOnLeftRelease(activeStack))
			{
				this.pswg$tickLeftActiveItemStack();
			}
		}

		this.pswg$clearLeftActiveItem();
	}

	@Override
	public void pswg$tickLeftActiveItemStack()
	{
		var self = (LivingEntity)(Object)this;

		if (this.pswg$isLeftUsingItem())
		{
			if (ItemStack.areItemsEqual(self.getStackInHand(self.getActiveHand()), this.pswg$getLeftActiveItemStack()))
			{
				this.pswg$setLeftActiveItemStack(self.getStackInHand(self.getActiveHand()));
				this.pswg$tickItemStackLeftUsage(this.pswg$getLeftActiveItemStack());
			}
			else
			{
				this.pswg$clearLeftActiveItem();
			}
		}
	}

	@Override
	public void pswg$tickItemStackLeftUsage(ItemStack stack)
	{
		var self = (LivingEntity)(Object)this;

		if (!(stack.getItem() instanceof ILeftClickUsable leftClickingItem))
			throw new RuntimeException("Attempted to tick usage of non-left-clicking item");

		leftClickingItem.usageTickLeft(self.getWorld(), self, stack, this.pswg$getItemLeftUseTimeLeft());
		this.pswg$setItemLeftUseTimeLeft(this.pswg$getItemLeftUseTimeLeft() - 1);

		if (this.pswg$getItemLeftUseTimeLeft() == 0 && !self.getWorld().isClient() && !leftClickingItem.isUsedOnLeftRelease(stack))
		{
			this.pswg$consumeLeftItem();
		}
	}

	@Override
	public void pswg$consumeLeftItem()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.getWorld().isClient() || this.pswg$isLeftUsingItem())
		{
			Hand hand = self.getActiveHand();
			if (!this.pswg$getLeftActiveItemStack().equals(self.getStackInHand(hand)))
			{
				this.pswg$stopLeftUsingItem();
			}
			else
			{
				if (!this.pswg$getLeftActiveItemStack().isEmpty() && this.pswg$isLeftUsingItem())
				{
					var activeStack = this.pswg$getLeftActiveItemStack();
					if (!(activeStack.getItem() instanceof ILeftClickUsable leftClickingItem))
						throw new RuntimeException("Attempted to finish usage of non-left-clicking item");

					ItemStack itemStack = leftClickingItem.finishUsingLeft(activeStack, self.getWorld(), self);
					if (itemStack != this.pswg$getLeftActiveItemStack())
					{
						self.setStackInHand(hand, itemStack);
					}

					this.pswg$clearLeftActiveItem();
				}
			}
		}
	}

	@Override
	public void pswg$setCurrentHandLeft(Hand hand)
	{
		var self = (LivingEntity)(Object)this;

		ItemStack itemStack = self.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.pswg$isLeftUsingItem())
		{
			this.pswg$setLeftActiveItemStack(itemStack);

			if (!(itemStack.getItem() instanceof ILeftClickUsable leftClickingItem))
				throw new RuntimeException("Attempted to finish usage of non-left-clicking item");

			this.pswg$setItemLeftUseTimeLeft(leftClickingItem.getMaxUseLeftTime(itemStack, self));

			if (!self.getWorld().isClient())
			{
				this.pswg$setLeftUsingItem(true);

				// TODO: is this ever used in vanilla?
				// self.setLivingFlag(self.OFF_HAND_ACTIVE_FLAG, hand == Hand.OFF_HAND);

				self.emitGameEvent(GameEvent.ITEM_INTERACT_START);
			}
		}
	}

	@Override
	public void pswg$clearLeftActiveItem()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.getWorld().isClient())
		{
			boolean wasUsingItem = this.pswg$isLeftUsingItem();
			this.pswg$setLeftUsingItem(false);

			if (wasUsingItem)
				self.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
		}

		this.pswg$setLeftActiveItemStack(ItemStack.EMPTY);
		this.pswg$setItemLeftUseTimeLeft(0);
	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickActiveItemStack()V", shift = At.Shift.AFTER))
	private void afterTickActiveItemStack(CallbackInfo ci)
	{
		this.pswg$tickLeftActiveItemStack();
	}
}
