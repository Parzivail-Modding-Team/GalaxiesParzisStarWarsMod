package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.Seat;
import com.parzivail.util.common.Pair;
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
			Pair<BasicFlightModel, Seat> pair = EntityUtils.getShipRiding(event.entity);
			if (pair != null && pair.left != null && event.isCancelable())
			{
				BasicFlightModel ship = pair.left;
				event.setCanceled(true);
				if (event.entity == StarWarsGalaxy.mc.thePlayer)
				{
					FxMC.changeCameraDist(10);
					FxMC.changeCameraRoll(ship.orientation.getRoll());
					ship.rotationYaw = 180 - ship.orientation.getYaw();
					ship.rotationPitch = ship.orientation.getPitch();
					FxMC.changePrevCameraRoll(ship.previousOrientation.getRoll());
					ship.prevRotationYaw = 180 - ship.previousOrientation.getYaw();
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
		//		else if (StarWarsGalaxy.mc.gameSettings.showDebugInfo)
		//		{
		//			GL.PushMatrix();
		//			GL.Translate(event.x, event.y + (event.entity.boundingBox.maxY - event.entity.boundingBox.minY) + 1, event.z);
		//
		//			float f = 0.025f;
		//			String n = event.entity.getClass().getSimpleName();
		//
		//			FontRenderer fontrenderer = StarWarsGalaxy.mc.fontRenderer;
		//			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		//			GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
		//			GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		//			GL11.glScalef(-f, -f, f);
		//			GL11.glDisable(GL11.GL_LIGHTING);
		//			GL11.glDepthMask(false);
		//			GL11.glDisable(GL11.GL_DEPTH_TEST);
		//			GL11.glEnable(GL11.GL_BLEND);
		//			fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0x40ffffff);
		//			GL11.glEnable(GL11.GL_DEPTH_TEST);
		//			GL11.glDepthMask(true);
		//			fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0xffffffff);
		//			GL11.glEnable(GL11.GL_LIGHTING);
		//			GL11.glDisable(GL11.GL_BLEND);
		//
		//			GL.PopMatrix();
		//		}
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
