package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.ui.FxMC;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Pre event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			BasicFlightModel ship = EntityUtils.getShipRiding(event.entity);
			if (ship != null && event.isCancelable())
			{
				event.setCanceled(true);
				if (event.entity == StarWarsGalaxy.mc.thePlayer)
				{
					FxMC.changeCameraDist(10);
					FxMC.changeCameraRoll(ship.orientation.getRoll());
					ship.rotationYaw = ship.orientation.getYaw();
					ship.rotationPitch = ship.orientation.getPitch();
					FxMC.changePrevCameraRoll(ship.previousOrientation.getRoll());
					ship.prevRotationYaw = ship.previousOrientation.getYaw();
					ship.prevRotationPitch = ship.previousOrientation.getPitch();
					StarWarsGalaxy.mc.renderViewEntity = ship;
				}
				else
				{
					FxMC.changeCameraRoll(0);
					StarWarsGalaxy.mc.renderViewEntity = StarWarsGalaxy.mc.thePlayer;
				}
			}
			else
			{
				FxMC.changeCameraRoll(0);
				StarWarsGalaxy.mc.renderViewEntity = StarWarsGalaxy.mc.thePlayer;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		KeyHandler.onKeyInput(event);
	}

	//	@SubscribeEvent
	//	@SideOnly(Side.CLIENT)
	//	public void onRenderGui(RenderGameOverlayEvent.Post event)
	//	{
	//		Minecraft mc = Minecraft.getMinecraft();
	//	}
}
