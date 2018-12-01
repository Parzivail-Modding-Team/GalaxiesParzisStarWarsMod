package com.parzivail.util.ui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.EntityRenderer;
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
			ReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, dist, "thirdPersonDistance", "field_78490_B", "E");
			ReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, dist, "thirdPersonDistanceTemp", "field_78491_C", "F");
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
			ReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, roll, "camRoll", "field_78495_O", "R");
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
			ReflectionHelper.setPrivateValue(EntityRenderer.class, Client.mc.entityRenderer, roll, "prevCamRoll", "field_78505_P", "S");
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
}
