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
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

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
				{
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glPushAttrib(GL11.GL_LINE_BIT);

					GL.PushMatrix();
					Minecraft mc = Minecraft.getMinecraft();
					ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
					GL.Translate(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2, 0);
					mc.entityRenderer.disableLightmap(0);
					GL.Disable(EnableCap.Lighting);
					GL.Disable(EnableCap.Blend);
					GL.Disable(EnableCap.Texture2D);

					((ICustomCrosshair)heldItem.getItem()).drawCrosshair(sr, StarWarsGalaxy.mc.thePlayer);

					GL.PopMatrix();
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glPopAttrib();
					GL11.glPopAttrib();
				}
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
