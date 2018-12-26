package com.parzivail.util.ui;

import com.parzivail.swg.gui.GuiScreenPlanetEnter;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.WorldRegister;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class PLoadingScreenRenderer extends LoadingScreenRenderer
{
	Framebuffer fb;
	private long time = Minecraft.getSystemTime();

	public PLoadingScreenRenderer(Minecraft mc)
	{
		super(mc);

		fb = ObfuscationReflectionHelper.getPrivateValue(LoadingScreenRenderer.class, this, "field_146588_g");
	}

	/**
	 * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
	 */
	public void setLoadingProgress(int progress)
	{
		World worldEntering = Client.mc.theWorld;
		if (Client.mc.getNetHandler() != null)
			worldEntering = ObfuscationReflectionHelper.getPrivateValue(NetHandlerPlayClient.class, Client.mc.getNetHandler(), "clientWorldController");

		String c = getCurrentlyDisplayedText();
		String fd = getField_73727_a();

		if (worldEntering == null || !WorldRegister.planetDescriptorHashMap.containsKey(worldEntering.provider.dimensionId) || (!c.equals(I18n.format("menu.loadingLevel")) && !c.equals("")))
		{
			super.setLoadingProgress(progress);
			return;
		}

		long j = Minecraft.getSystemTime();

		if (j - time >= 100L)
		{
			time = j;
			ScaledResolution scaledresolution = Client.resolution;
			int k = scaledresolution.getScaleFactor();
			int l = scaledresolution.getScaledWidth();
			int i1 = scaledresolution.getScaledHeight();

			if (OpenGlHelper.isFramebufferEnabled())
			{
				fb.framebufferClear();
			}
			else
			{
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			}

			fb.bindFramebuffer(false);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -200.0F);

			if (!OpenGlHelper.isFramebufferEnabled())
			{
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			}

			if (!FMLClientHandler.instance().handleLoadingScreen(scaledresolution))
			{
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);

				String status = c;

				if (c.equals(""))
					status = fd;
				else if (!fd.equals(""))
					status = String.format("%s - %s", c, fd);

				GuiScreenPlanetEnter.renderPlanetEnterBackdrop(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), worldEntering.provider.dimensionId, status);
			}

			fb.unbindFramebuffer();

			if (OpenGlHelper.isFramebufferEnabled())
			{
				fb.framebufferRender(l * k, i1 * k);
			}

			Client.mc.resetSize();

			try
			{
				Thread.yield();
			}
			catch (Exception ignored)
			{
			}
		}
	}

	private String getCurrentlyDisplayedText()
	{
		return ObfuscationReflectionHelper.getPrivateValue(LoadingScreenRenderer.class, this, "currentlyDisplayedText");
	}

	private String getField_73727_a()
	{
		return ObfuscationReflectionHelper.getPrivateValue(LoadingScreenRenderer.class, this, "field_73727_a");
	}
}
