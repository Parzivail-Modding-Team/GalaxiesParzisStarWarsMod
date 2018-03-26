package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ICustomCrosshair;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.ClientRenderState;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.Seat;
import com.parzivail.util.common.Pair;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.ui.FxMC;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
			ShaderHelper.useShader(ShaderHelper.entityGlow);
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
		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
			ShaderHelper.releaseShader();
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

			ItemStack heldItem = StarWarsGalaxy.mc.thePlayer.getHeldItem();

			if (heldItem != null && heldItem.getItem() instanceof ICustomCrosshair)
			{
				if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && event.isCancelable())
					event.setCanceled(true);

				if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
					((ICustomCrosshair)heldItem.getItem()).drawCrosshair(StarWarsGalaxy.mc.thePlayer);
			}

			KeybindRegistry.keyAttack.setIntercepting(heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor);

			if (heldItem == null || !(heldItem.getItem() instanceof PItem) || !(((PItem)heldItem.getItem()).shouldRequestRenderState()))
				ClientRenderState.renderState.removeAll(ClientRenderState.renderStateRequest.values());
			else
			{

				if (ClientRenderState.renderStateRequest.keySet().contains(heldItem.getItem().getClass()))
				{
					ClientRenderState request = ClientRenderState.renderStateRequest.get(heldItem.getItem().getClass());
					if (request != null && !ClientRenderState.renderState.contains(request))
						ClientRenderState.renderState.add(request);
				}
				else
					ClientRenderState.renderState.removeAll(ClientRenderState.renderStateRequest.values());
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		KeyHandler.onInput(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.MouseInputEvent event)
	{
		KeyHandler.onInput(event);
	}
}
