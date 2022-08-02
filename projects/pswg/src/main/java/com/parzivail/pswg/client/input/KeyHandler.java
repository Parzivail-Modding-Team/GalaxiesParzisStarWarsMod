package com.parzivail.pswg.client.input;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.screen.CharacterScreen;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.util.item.ItemAction;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.util.EnumSet;

public class KeyHandler
{
	public static void tick(MinecraftClient mc)
	{
		if (mc.player == null)
			return;

		if (Client.KEY_SPECIES_SELECT.wasPressed())
		{
			mc.setScreen(new CharacterScreen(mc.currentScreen));
		}

		if (Client.KEY_PRIMARY_ITEM_ACTION.wasPressed())
		{
			sendAction(ItemAction.PRIMARY);
		}

		if (Client.KEY_SECONDARY_ITEM_ACTION.wasPressed())
		{
			sendAction(ItemAction.SECONDARY);
		}

		var ship = ShipEntity.getShip(mc.player);

		if (ship != null)
		{
			var controls = EnumSet.noneOf(ShipControls.class);

			if (mc.options.forwardKey.isPressed())
				controls.add(ShipControls.THROTTLE_UP);

			if (mc.options.backKey.isPressed())
				controls.add(ShipControls.THROTTLE_DOWN);

			if (mc.options.rightKey.isPressed())
				controls.add(ShipControls.SPECIAL1);

			if (mc.options.leftKey.isPressed())
				controls.add(ShipControls.SPECIAL2);

			ship.acceptControlInput(controls);
		}

		var jetpack = JetpackItem.getEquippedJetpack(mc.player);
		if (!jetpack.isEmpty())
		{
			var originalControls = ((IJetpackDataContainer)mc.player).pswg_getJetpackControls();
			var controls = EnumSet.noneOf(JetpackControls.class);

			if (mc.options.forwardKey.isPressed())
				controls.add(JetpackControls.FORWARD);

			if (mc.options.backKey.isPressed())
				controls.add(JetpackControls.BACKWARD);

			if (mc.options.rightKey.isPressed())
				controls.add(JetpackControls.RIGHT);

			if (mc.options.leftKey.isPressed())
				controls.add(JetpackControls.LEFT);

			if (mc.options.sprintKey.isPressed())
				controls.add(JetpackControls.MODE);

			if (mc.options.jumpKey.isPressed())
				controls.add(JetpackControls.ASCEND);

			if (mc.options.sneakKey.isPressed())
				controls.add(JetpackControls.DESCEND);

			if (!controls.equals(originalControls))
			{
				var passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeShort(JetpackControls.pack(controls));
				ClientPlayNetworking.send(SwgPackets.C2S.JetpackControls, passedData);
			}

			((IJetpackDataContainer)mc.player).pswg_setJetpackControls(controls);
		}
	}

	private static void sendAction(ItemAction action)
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeInt(action.ordinal());
		ClientPlayNetworking.send(SwgPackets.C2S.PlayerItemAction, passedData);
	}
}
