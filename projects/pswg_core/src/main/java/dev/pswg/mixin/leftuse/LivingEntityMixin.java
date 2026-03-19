package dev.pswg.mixin.leftuse;

import dev.pswg.interaction.ILeftClickingEntity;
import dev.pswg.interaction.LeftClickingEntityAttachment;
import dev.pswg.interaction.ServerPlayerAction;
import dev.pswg.item.ILeftClickUsable;
import dev.pswg.networking.GalaxiesPlayerActionS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
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
	@Override
	public boolean pswg$isLeftUsingItem()
	{
		var self = (LivingEntity)(Object)this;

		return LeftClickingEntityAttachment
				.get(self)
				.isUsingItemLeft();
	}

	@Override
	public void pswg$setLeftUsingItem(boolean isLeftUsing)
	{
		var self = (LivingEntity)(Object)this;

		LeftClickingEntityAttachment
				.get(self)
				.withIsUsingItemLeft(isLeftUsing)
				.set(self);

		if (this.pswg$isLeftUsingItem() && this.pswg$getLeftActiveItemStack().isEmpty())
		{
			var activeStack = self.getItemInHand(self.getUsedItemHand());
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
		var self = (LivingEntity)(Object)this;

		return LeftClickingEntityAttachment
				.get(self)
				.itemLeftUseTimeLeft();
	}

	@Override
	public void pswg$setItemLeftUseTimeLeft(int timeLeft)
	{
		var self = (LivingEntity)(Object)this;

		// TODO: does this need to sync with the client? it's called every tick
		LeftClickingEntityAttachment
				.get(self)
				.withItemLeftUseTimeLeft(timeLeft)
				.set(self);
	}

	@Override
	public ItemStack pswg$getLeftActiveItemStack()
	{
		var self = (LivingEntity)(Object)this;

		return LeftClickingEntityAttachment
				.get(self)
				.leftActiveItemStack();
	}

	@Override
	public void pswg$setLeftActiveItemStack(ItemStack stack)
	{
		var self = (LivingEntity)(Object)this;

		LeftClickingEntityAttachment
				.get(self)
				.withLeftActiveItemStack(stack)
				.set(self);
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

			leftClickingItem.onStoppedUsingLeft(activeStack, self.level(), self, this.pswg$getItemLeftUseTimeLeft());
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
			if (ItemStack.isSameItem(self.getItemInHand(self.getUsedItemHand()), this.pswg$getLeftActiveItemStack()))
			{
				this.pswg$setLeftActiveItemStack(self.getItemInHand(self.getUsedItemHand()));
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

		leftClickingItem.usageTickLeft(self.level(), self, stack, this.pswg$getItemLeftUseTimeLeft());
		this.pswg$setItemLeftUseTimeLeft(this.pswg$getItemLeftUseTimeLeft() - 1);

		if (this.pswg$getItemLeftUseTimeLeft() == 0 && !self.level().isClientSide() && !leftClickingItem.isUsedOnLeftRelease(stack))
		{
			this.pswg$consumeLeftItem();
		}
	}

	@Override
	public void pswg$consumeLeftItem()
	{
		var self = (LivingEntity)(Object)this;

		if (self instanceof ServerPlayer player)
		{
			if (!this.pswg$getLeftActiveItemStack().isEmpty() && this.pswg$isLeftUsingItem())
			{
				ServerPlayNetworking.send(player, new GalaxiesPlayerActionS2CPacket(ServerPlayerAction.CONSUME_LEFT_ITEM));
				consumeItemCore();
			}
		}
		else
			consumeItemCore();
	}

	@Unique
	private void consumeItemCore()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.level().isClientSide() || this.pswg$isLeftUsingItem())
		{
			InteractionHand hand = self.getUsedItemHand();
			if (!ItemStack.matches(this.pswg$getLeftActiveItemStack(), self.getItemInHand(hand)))
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

					ItemStack itemStack = leftClickingItem.finishUsingLeft(activeStack, self.level(), self);
					if (itemStack != this.pswg$getLeftActiveItemStack())
					{
						self.setItemInHand(hand, itemStack);
					}

					this.pswg$clearLeftActiveItem();
				}
			}
		}
	}

	@Override
	public void pswg$setCurrentHandLeft(InteractionHand hand)
	{
		var self = (LivingEntity)(Object)this;

		ItemStack itemStack = self.getItemInHand(hand);
		if (!itemStack.isEmpty() && !this.pswg$isLeftUsingItem())
		{
			this.pswg$setLeftActiveItemStack(itemStack);

			if (!(itemStack.getItem() instanceof ILeftClickUsable leftClickingItem))
				throw new RuntimeException("Attempted to finish usage of non-left-clicking item");

			this.pswg$setItemLeftUseTimeLeft(leftClickingItem.getMaxUseLeftTime(itemStack, self));

			if (!self.level().isClientSide())
			{
				this.pswg$setLeftUsingItem(true);

				// TODO: is this ever used in vanilla?
				// self.setLivingFlag(self.OFF_HAND_ACTIVE_FLAG, hand == Hand.OFF_HAND);

				self.gameEvent(GameEvent.ITEM_INTERACT_START);
			}
		}
	}

	@Override
	public void pswg$clearLeftActiveItem()
	{
		var self = (LivingEntity)(Object)this;

		if (!self.level().isClientSide())
		{
			boolean wasUsingItem = this.pswg$isLeftUsingItem();
			this.pswg$setLeftUsingItem(false);

			if (wasUsingItem)
				self.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
		}

		this.pswg$setLeftActiveItemStack(ItemStack.EMPTY);
		this.pswg$setItemLeftUseTimeLeft(0);
	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updatingUsingItem()V", shift = At.Shift.AFTER))
	private void afterTickActiveItemStack(CallbackInfo ci)
	{
		this.pswg$tickLeftActiveItemStack();
	}
}
