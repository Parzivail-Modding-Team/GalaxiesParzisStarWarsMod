package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.gui.GuiNowEntering;
import com.parzivail.swg.gui.GuiScreenTrailer;
import com.parzivail.swg.item.ICustomCrosshair;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.IScreenShader;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.decal.WorldDecals;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.common.Lumberjack;
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
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(GuiScreenEvent.InitGuiEvent.Pre event)
	{
		if (event.gui instanceof GuiMainMenu && !StarWarsGalaxy.config.getHasSeenIntroCrawl())
		{
			Client.mc.displayGuiScreen(new GuiScreenTrailer());
			StarWarsGalaxy.config.setHasSeenIntroCrawl(true);
		}

		//		if (event.gui instanceof GuiDownloadTerrain)
		//		{
		//			Client.mc.displayGuiScreen(new GuiScreenPlanetEnter(Client.mc.getNetHandler(), Client.mc.thePlayer.dimension));
		//		}
	}

	@SubscribeEvent
	public void on(WorldEvent.Load loadEvent)
	{
		try
		{
			FileHandler.saveNbtMappings(loadEvent.world);
		}
		catch (NullPointerException e)
		{
			Lumberjack.debug("Couldn't save NBT map. Probably connecting to a server.");
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.RenderTickEvent event)
	{
		if (event.phase != TickEvent.Phase.START)
			return;

		Client.renderPartialTicks = event.renderTickTime;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Pre event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			BasicFlightModel ship = EntityUtils.getShipRiding(event.entity);
			if (ship != null && event.isCancelable())
				event.setCanceled(true);
		}
		//		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
		//			ShaderHelper.useShader(ShaderHelper.entityGlow);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Post event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			BasicFlightModel ship = EntityUtils.getShipRiding(event.entity);
			if (ship != null && event.isCancelable())
				event.setCanceled(true);
		}
		//		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
		//			ShaderHelper.releaseShader();
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
			BasicFlightModel ship = EntityUtils.getShipRiding(Client.mc.thePlayer);
			if (ship == null)
			{
				FxMC.changeCameraRoll(0);
				Client.mc.renderViewEntity = Client.mc.thePlayer;
			}
			else
			{
				FxMC.changeCameraDist(10);
				float r = ship.orientation.getRoll();
				FxMC.changeCameraRoll(r);
				Client.mc.renderViewEntity = ship;
			}

			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			ScaledResolution sr = new ScaledResolution(Client.mc, Client.mc.displayWidth, Client.mc.displayHeight);
			Client.resolution = sr;

			if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
			{
				GuiNowEntering.draw(sr, Client.mc.thePlayer);
			}

			if (heldItem != null && heldItem.getItem() instanceof IScreenShader)
				ShaderHelper.framebufferShader = ((IScreenShader)heldItem.getItem()).requestShader(Client.mc.thePlayer, heldItem);
			else
				ShaderHelper.framebufferShader = 0;

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

			KeybindRegistry.keyAttack.setIntercepting(heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor);
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
