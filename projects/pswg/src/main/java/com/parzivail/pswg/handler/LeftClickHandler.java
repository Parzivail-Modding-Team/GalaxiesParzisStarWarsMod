package com.parzivail.pswg.handler;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
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

		var stack = minecraft.player.getMainHandStack();

		// Repeated events
		if (stack.getItem() instanceof ILeftClickConsumer lcc)
		{
			if (!minecraft.options.attackKey.isPressed())
				return;

			ci.cancel();

			if (lcc.allowRepeatedLeftHold(minecraft.player.world, minecraft.player, Hand.MAIN_HAND))
			{
				if (useItemLeft(ci, minecraft.player, lcc))
					return;

				interactionManager.cancelBlockBreaking();
			}
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

	private static boolean useItemLeft(CallbackInfo ci, PlayerEntity player, ILeftClickConsumer lcc)
	{
		if (player.isSpectator())
			return false;

		var tar = lcc.useLeft(player.world, player, Hand.MAIN_HAND);
		var ar = tar.getResult();

		if (ar == ActionResult.PASS)
			return false;

		ClientPlayNetworking.send(SwgPackets.C2S.PlayerLeftClickItem, new PacketByteBuf(Unpooled.buffer()));
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

		// Single-fire events
		if (stack.getItem() instanceof ILeftClickConsumer lcc)
		{
			useItemLeft(cir, minecraft.player, lcc);
			cir.setReturnValue(false);
		}
	}
}
