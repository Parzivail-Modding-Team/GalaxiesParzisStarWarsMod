package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHyperspaceLoading extends GuiScreen
{
	private boolean isEntering;

	public GuiHyperspaceLoading()
	{

	}

	public void setEntering(boolean isEntering)
	{
		this.isEntering = isEntering;
	}

	@Override
	public void initGui()
	{
		this.buttonList.clear();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		FontRenderer fontRenderer = mc.fontRenderer;
		ScaledResolution resolution = new ScaledResolution(mc);

		drawBackground(-1);

		String loadTitle = I18n.translateToLocal(Resources.MODID + ".loading.title." + (isEntering ? "enter" : "leave"));
		GlStateManager.pushMatrix();
		GlStateManager.translate((resolution.getScaledWidth() / 2f) - (fontRenderer.getStringWidth(loadTitle) / 4f), (resolution.getScaledHeight() / 3f), 0f);
		GlStateManager.translate(-(fontRenderer.getStringWidth(loadTitle) / 4f), 0f, 0f);
		fontRenderer.drawStringWithShadow(loadTitle, 0, 0, 0xEEEEEE); //eeeeeeeeeeeeeeeeee
		GlStateManager.popMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}
