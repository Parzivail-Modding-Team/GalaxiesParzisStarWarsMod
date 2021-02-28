package com.parzivail.pswg.handler;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.item.ILeftClickConsumer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class LeftClickHandler
{
	public static void handleInputEvents(CallbackInfo ci, @NotNull ClientPlayerInteractionManager interactionManager)
	{
		MinecraftClient mc = Client.minecraft;

		if (!mc.options.keyAttack.isPressed())
			return;

		assert mc.player != null;

		ShipEntity ship = ShipEntity.getShip(mc.player);
		if (ship != null)
			return;

		ItemStack stack = mc.player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
		{
			TypedActionResult<ItemStack> tar = ((ILeftClickConsumer)stack.getItem()).useLeft(mc.player.world, mc.player, Hand.MAIN_HAND);
			ActionResult ar = tar.getResult();

			if (ar == ActionResult.PASS)
				return;

			ci.cancel();

			interactionManager.cancelBlockBreaking();

			ClientPlayNetworking.send(SwgPackets.C2S.PacketPlayerLeftClickItem, new PacketByteBuf(Unpooled.buffer()));
		}
	}

	public static void doAttack(CallbackInfo ci)
	{
		MinecraftClient mc = Client.minecraft;

		assert mc.player != null;

		ShipEntity ship = ShipEntity.getShip(mc.player);
		if (ship != null)
		{
			ship.acceptLeftClick();

			ci.cancel();
			return;
		}

		ItemStack stack = mc.player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
			ci.cancel();
	}
}
