package com.parzivail.pswg.handler;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.util.item.ILeftClickConsumer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LeftClickHandler
{
	public static void handleInputEvents(Runnable doAttackDelegate, @NotNull ClientPlayerInteractionManager interactionManager, CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);
		if (ship != null && !ship.usePlayerPerspective())
			return;

		if (!minecraft.options.attackKey.isPressed())
			return;

		// Repeated left-click events
		if (BlasterItem.isDualWielding(minecraft.player) && minecraft.player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ILeftClickConsumer lcc)
		{
			handleLccInputEvents(interactionManager, ci, minecraft, Hand.OFF_HAND, lcc);
			return;
		}

		var stack = minecraft.player.getMainHandStack();
		if (stack.getItem() instanceof ILeftClickConsumer lcc)
			handleLccInputEvents(interactionManager, ci, minecraft, Hand.MAIN_HAND, lcc);
	}

	private static void handleLccInputEvents(@NotNull ClientPlayerInteractionManager interactionManager, CallbackInfo ci, MinecraftClient minecraft, Hand hand, ILeftClickConsumer lcc)
	{
		assert minecraft.player != null;

		ci.cancel();

		if (lcc.allowRepeatedLeftHold(minecraft.player.world, minecraft.player, hand))
		{
			if (useItemLeft(minecraft.player, lcc, hand, true))
				return;

			interactionManager.cancelBlockBreaking();
		}
	}

	public static void handleIsUsingItemAttack(Runnable doAttackDelegate, CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		assert minecraft.player != null;

		var stack = minecraft.player.getMainHandStack();

		// Repeated events
		if (stack.getItem() instanceof ILeftClickConsumer)
		{
			if (minecraft.player.isUsingItem())
				while (minecraft.options.attackKey.wasPressed())
					doAttackDelegate.run();
		}
	}

	private static boolean useItemLeft(PlayerEntity player, ILeftClickConsumer lcc, Hand hand, boolean isRepeatEvent)
	{
		if (player.isSpectator())
			return false;

		var tar = lcc.useLeft(player.world, player, hand, isRepeatEvent);
		var ar = tar.getResult();

		if (ar == ActionResult.PASS)
			return false;

		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeInt(hand.ordinal());
		passedData.writeBoolean(isRepeatEvent);
		ClientPlayNetworking.send(SwgPackets.C2S.PlayerLeftClickItem, passedData);
		return true;
	}

	public static void doAttack(CallbackInfoReturnable<Boolean> cir)
	{
		var minecraft = MinecraftClient.getInstance();

		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);
		if (ship != null && ship.acceptLeftClick(minecraft.player))
		{
			cir.setReturnValue(false);
			return;
		}

		var stack = minecraft.player.getMainHandStack();

		if (BlasterItem.isDualWielding(minecraft.player) && minecraft.player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ILeftClickConsumer lcc)
		{
			useItemLeft(minecraft.player, lcc, Hand.OFF_HAND, false);
			cir.setReturnValue(false);
			return;
		}

		// Single-fire events
		if (stack.getItem() instanceof ILeftClickConsumer lcc)
		{
			useItemLeft(minecraft.player, lcc, Hand.MAIN_HAND, false);
			cir.setReturnValue(false);
		}
	}
}
