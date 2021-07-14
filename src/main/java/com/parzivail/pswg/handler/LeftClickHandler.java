package com.parzivail.pswg.handler;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.item.ILeftClickConsumer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class LeftClickHandler
{
	public static void handleInputEvents(CallbackInfo ci, @NotNull ClientPlayerInteractionManager interactionManager)
	{
		var minecraft = MinecraftClient.getInstance();

		if (!minecraft.options.keyAttack.isPressed())
			return;

		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);
		if (ship != null)
			return;

		var stack = minecraft.player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
		{
			var tar = ((ILeftClickConsumer)stack.getItem()).useLeft(minecraft.player.world, minecraft.player, Hand.MAIN_HAND);
			var ar = tar.getResult();

			if (ar == ActionResult.PASS)
				return;

			ci.cancel();

			interactionManager.cancelBlockBreaking();

			ClientPlayNetworking.send(SwgPackets.C2S.PacketPlayerLeftClickItem, new PacketByteBuf(Unpooled.buffer()));
		}
	}

	public static void doAttack(CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);
		if (ship != null)
		{
			ship.acceptLeftClick();

			ci.cancel();
			return;
		}

		var stack = minecraft.player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
			ci.cancel();
	}
}
