package com.parzivail.pswg.client.input;

import com.parzivail.pswg.entity.ChaseCamEntity;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.util.client.GameRendererExt;
import com.parzivail.util.client.GameRendererExtUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ShipInputHandler
{
	@Environment(EnvType.CLIENT)
	public static boolean handle(double cursorDeltaX, double cursorDeltaY)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;

		assert player != null;

		GameRendererExt gr = GameRendererExtUtil.from(mc.gameRenderer);
		ShipEntity ship = ShipEntity.getShip(player);

		if (ship != null)
		{
			if (mc.options.perspective != 0)
				mc.cameraEntity = ship.camera;
			else
				mc.cameraEntity = ship;

			gr.setHandVisible(false);

			ship.acceptInput(cursorDeltaX, cursorDeltaY);
			return true;
		}

		if (mc.cameraEntity instanceof ChaseCamEntity || mc.cameraEntity instanceof ShipEntity)
		{
			mc.cameraEntity = player;
			gr.setHandVisible(true);
		}

		return false;
	}
}
