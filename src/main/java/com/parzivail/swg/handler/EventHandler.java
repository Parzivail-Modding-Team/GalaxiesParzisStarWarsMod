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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
				event.setCanceled(true);
		}
		//else if (event.entity instanceof EntityLiving && StarWarsGalaxy.mc.thePlayer.getHeldItem().getItem() == ItemRegister.slugRifle)
		//	ShaderHelper.useShader(ShaderHelper.entityGlow);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Post event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			Pair<BasicFlightModel, Seat> pair = EntityUtils.getShipRiding(event.entity);
			if (pair != null && pair.left != null && event.isCancelable())
				event.setCanceled(true);
		}
		//else if (event.entity instanceof EntityLiving && StarWarsGalaxy.mc.thePlayer.getHeldItem().getItem() == ItemRegister.slugRifle)
		//	ShaderHelper.releaseShader();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderGui(RenderGameOverlayEvent.Pre event)
	{
		if (StarWarsGalaxy.mc.thePlayer != null)
		{
			Pair<BasicFlightModel, Seat> pair = EntityUtils.getShipRiding(StarWarsGalaxy.mc.thePlayer);
			if (pair == null || pair.left == null)
			{
				FxMC.changeCameraRoll(0);
				StarWarsGalaxy.mc.renderViewEntity = StarWarsGalaxy.mc.thePlayer;
			}
			else
			{
				FxMC.changeCameraDist(10);
				float r = pair.left.orientation.getRoll();
				FxMC.changeCameraRoll(r);
				StarWarsGalaxy.mc.renderViewEntity = pair.left;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		KeyHandler.onKeyInput(event);
	}
}
