package com.parzivail.util.ui;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.EntityRenderer;

/**
 * Created by colby on 12/27/2017.
 */
public class FxMC
{
	public static void changeCameraDist(int dist)
	{
		try
		{
			ReflectionHelper.setPrivateValue(EntityRenderer.class, StarWarsGalaxy.mc.entityRenderer, dist, "thirdPersonDistance", "field_78490_B", "E");
			ReflectionHelper.setPrivateValue(EntityRenderer.class, StarWarsGalaxy.mc.entityRenderer, dist, "thirdPersonDistanceTemp", "field_78491_C", "F");
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
			ReflectionHelper.setPrivateValue(EntityRenderer.class, StarWarsGalaxy.mc.entityRenderer, roll, "camRoll", "field_78495_O", "R");
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
			ReflectionHelper.setPrivateValue(EntityRenderer.class, StarWarsGalaxy.mc.entityRenderer, roll, "prevCamRoll", "field_78505_P", "S");
		}
		catch (Exception e)
		{
			Lumberjack.warn("Unable to change camera roll!");
			e.printStackTrace();
		}
	}
}
