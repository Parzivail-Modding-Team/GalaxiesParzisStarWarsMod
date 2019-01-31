package com.parzivail.util.ui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/27/2017.
 */
public class FxMC
{
	public static void changeCameraDist(int dist)
	{
		try
		{
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, dist, "thirdPersonDistance", "field_78490_B", "E");
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, dist, "thirdPersonDistanceTemp", "field_78491_C", "F");
		}
		catch (Exception e)
		{
			Lumberjack.warn("Unable to change camera distance!");
			e.printStackTrace();
		}
	}

	public static void changeCameraRoll(float roll)
	{
		try
		{
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, roll, "camRoll", "field_78495_O", "R");
		}
		catch (Exception e)
		{
			Lumberjack.warn("Unable to change camera roll!");
			e.printStackTrace();
		}
	}

	public static void changePrevCameraRoll(float roll)
	{
		try
		{
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, roll, "prevCamRoll", "field_78505_P", "S");
		}
		catch (Exception e)
		{
			Lumberjack.warn("Unable to change camera roll!");
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void renderDirections()
	{
		GL.Disable(EnableCap.Texture2D);
		GL.Enable(EnableCap.ColorMaterial);
		GL11.glDepthMask(false);
		GL11.glLineWidth(4.0F);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Color(0, 0, 0);

		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3((double)10, 0.0D, 0.0D);

		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, (double)10, 0.0D);

		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, 0.0D, (double)10);
		GL.End();

		GL11.glLineWidth(2.0F);

		GL.Color(1f, 0, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3((double)10, 0.0D, 0.0D);
		GL.End();

		GL.Color(0, 1f, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, (double)10, 0.0D);
		GL.End();

		GL.Color(0, 0, 1f);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, 0.0D, (double)10);
		GL.End();

		GL11.glLineWidth(1.0F);
		GL11.glDepthMask(true);
		GL.Enable(EnableCap.Texture2D);
	}

	public static void enableSunBasedLighting(Entity entity, float partialTicks)
	{
		GL.Disable(EnableCap.Light0);
		GL.Disable(EnableCap.Light1);
		GL.Enable(EnableCap.Light7);
		double angle = entity.worldObj.getCelestialAngleRadians(partialTicks) + Math.PI / 2;
		float f1 = 1.0f;
		float f2 = 0.0f;
		float lX = (float)(100 * Math.cos(angle) + entity.posX);
		float lY = (float)(100 * Math.sin(angle) + entity.posY);
		float lZ = (float)entity.posZ;
		GL11.glLight(GL11.GL_LIGHT7, GL11.GL_POSITION, BufferUtil.setColorBuffer(lX, lY, lZ, 0.0D));
		GL11.glLight(GL11.GL_LIGHT7, GL11.GL_DIFFUSE, BufferUtil.setColorBuffer(f1, f1, f1, 1.0F));
		GL11.glLight(GL11.GL_LIGHT7, GL11.GL_AMBIENT, BufferUtil.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLight(GL11.GL_LIGHT7, GL11.GL_SPECULAR, BufferUtil.setColorBuffer(f2, f2, f2, 1.0F));
	}

	public static void disableSunBasedLighting()
	{
		GL.Enable(EnableCap.Light0);
		GL.Enable(EnableCap.Light1);
		GL.Disable(EnableCap.Light7);
	}
}
