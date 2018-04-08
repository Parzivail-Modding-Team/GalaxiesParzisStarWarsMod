package com.parzivail.swg.handler;

import com.parzivail.swg.item.ICustomCrosshair;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.ClientRenderState;
import com.parzivail.swg.render.WorldDecals;
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
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Pre event)
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
	public void on(RenderLivingEvent.Post event)
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
	public void on(RenderWorldLastEvent event)
	{
		WorldDecals.render(Minecraft.getMinecraft().thePlayer.dimension, event.partialTicks);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(FOVUpdateEvent event)
	{
		ItemStack stack = event.entity.getHeldItem();
		if (stack != null && stack.getItem() instanceof PItem)
		{
			PItem i = ((PItem)stack.getItem());
			if (i.shouldUsePrecisionMovement(stack, event.entity.worldObj, event.entity))
				event.newfov = event.fov /* * 1.334f*/ * i.getZoomLevel(stack, event.entity.worldObj, event.entity);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderGameOverlayEvent.Pre event)
	{
		if (Client.mc.thePlayer != null)
		{
			Pair<BasicFlightModel, Seat> pair = EntityUtils.getShipRiding(Client.mc.thePlayer);
			if (pair == null || pair.left == null)
			{
				FxMC.changeCameraRoll(0);
				Client.mc.renderViewEntity = Client.mc.thePlayer;
			}
			else
			{
				FxMC.changeCameraDist(10);
				float r = pair.left.orientation.getRoll();
				FxMC.changeCameraRoll(r);
				Client.mc.renderViewEntity = pair.left;
			}

			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			ScaledResolution sr = new ScaledResolution(Client.mc, Client.mc.displayWidth, Client.mc.displayHeight);
			if (heldItem != null && heldItem.getItem() instanceof ICustomCrosshair)
			{
				if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && event.isCancelable())
					event.setCanceled(true);

				if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
				{
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glPushAttrib(GL11.GL_LINE_BIT);
					GL11.glPushAttrib(GL11.GL_POINT_BIT);

					GL.PushMatrix();
					GL.Translate(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2, 0);
					Client.mc.entityRenderer.disableLightmap(0);
					GL.Disable(EnableCap.Lighting);
					GL.Disable(EnableCap.Texture2D);
					GL.Enable(EnableCap.Blend);
					GL.Enable(EnableCap.PointSmooth);
					GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);

					((ICustomCrosshair)heldItem.getItem()).drawCrosshair(sr, Client.mc.thePlayer, heldItem);

					GL.PopMatrix();
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glPopAttrib();
					GL11.glPopAttrib();
					GL11.glPopAttrib();
				}
			}

			if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
			{
				//				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				//				GL11.glPushAttrib(GL11.GL_LINE_BIT);
				//				GL11.glPushAttrib(GL11.GL_POINT_BIT);
				//				GL.PushMatrix();
				//				Client.mc.entityRenderer.disableLightmap(0);
				//				GL.Disable(EnableCap.Lighting);
				//				GL.Disable(EnableCap.Texture2D);
				//				GL.Enable(EnableCap.Blend);
				//				GL.Enable(EnableCap.PointSmooth);
				//				GL.Enable(EnableCap.LineSmooth);
				//				GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
				//				GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
				//				GuiHyperdrive.draw(sr, Client.mc.thePlayer);
				//				GL.PopMatrix();
				//				GL11.glColor4f(1, 1, 1, 1);
				//				GL11.glPopAttrib();
				//				GL11.glPopAttrib();
				//				GL11.glPopAttrib();
			}

			KeybindRegistry.keyAttack.setIntercepting(heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor);

			if (heldItem == null || !(heldItem.getItem() instanceof PItem) || !(((PItem)heldItem.getItem()).shouldRequestRenderState(heldItem, Client.mc.thePlayer.worldObj, Client.mc.thePlayer)))
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
	public void on(TickEvent.PlayerTickEvent event)
	{
		ItemStack stack = event.player.getHeldItem();
		if (stack != null && stack.getItem() instanceof PItem)
		{
			PItem i = ((PItem)stack.getItem());
			PItem.applyPrecisionMovement(event.player, i.shouldUsePrecisionMovement(stack, event.player.worldObj, event.player));
		}
		else
			PItem.applyPrecisionMovement(event.player, false);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.KeyInputEvent event)
	{
		KeyHandler.onInput(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.MouseInputEvent event)
	{
		KeyHandler.onInput(event);
	}
}
