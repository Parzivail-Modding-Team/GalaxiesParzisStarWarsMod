package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.GLPalette;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiQuestNotification extends Gui
{
	private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
	private int width;
	private String achievementTitle;
	private String achievementDescription;
	private long notificationTime;
	private boolean permanentNotification;

	public GuiQuestNotification()
	{
	}

	public void show()
	{
		achievementTitle = "Quest Started"; //I18n.format("achievement.get");
		achievementDescription = "Description";
		notificationTime = Minecraft.getSystemTime();
		permanentNotification = false;
	}

	private void resize()
	{
		ScaledResolution scaledresolution = new ScaledResolution(Client.mc, Client.mc.displayWidth, Client.mc.displayHeight);
		width = scaledresolution.getScaledWidth();
	}

	public void update()
	{
		if (/*theAchievement != null && */notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null)
		{
			double d0 = (double)(Minecraft.getSystemTime() - notificationTime) / 3000.0D;

			if (!permanentNotification)
			{
				if (d0 < 0.0D || d0 > 1.0D)
				{
					notificationTime = 0L;
					return;
				}
			}
			else if (d0 > 0.5D)
			{
				d0 = 0.5D;
			}

			resize();
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			double d1 = d0 * 2.0D;

			if (d1 > 1.0D)
			{
				d1 = 2.0D - d1;
			}

			d1 *= 4.0D;
			d1 = 1.0D - d1;

			if (d1 < 0.0D)
			{
				d1 = 0.0D;
			}

			d1 *= d1;
			d1 *= d1;
			int i = width - 160;
			int j = 0 - (int)(d1 * 36.0D);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Client.mc.getTextureManager().bindTexture(achievementBg);
			GL11.glDisable(GL11.GL_LIGHTING);
			drawTexturedModalRect(i, j, 96, 202, 160, 32);

			if (permanentNotification)
			{
				Client.mc.fontRendererObj.drawSplitString(achievementDescription, i + 7, j + 7, 120, -1);
			}
			else
			{
				Client.mc.fontRendererObj.drawString(achievementTitle, i + 7, j + 7, GLPalette.SW_YELLOW);
				Client.mc.fontRendererObj.drawString("\u00A7o" + achievementDescription, i + 7, j + 18, 0xFFFFFF);
			}
		}
	}

	public void clear()
	{
		notificationTime = 0L;
	}
}
