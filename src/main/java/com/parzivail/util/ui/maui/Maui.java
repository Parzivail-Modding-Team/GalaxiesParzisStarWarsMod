package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.io.InputStream;

public class Maui
{
	private static ResourceLocation deJaVuSansResource = Resources.location("font/DejaVuSans.ttf");
	public static TrueTypeFont deJaVuSans;

	static
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(deJaVuSansResource);
			InputStream inputStream = res.getInputStream();

			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(11f); // set font size
			deJaVuSans = new TrueTypeFont(awtFont2, true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
