package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerBlasterWorkbench;
import com.parzivail.tile.TileBlasterWorkbench;
import com.parzivail.util.ui.GLPalette;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBlasterWorkbench extends GuiContainer
{
	private static final ResourceLocation guiTexture = Resources.location("textures/container/blasterWorkbench.png");
	public TileBlasterWorkbench tile;

	public GuiBlasterWorkbench(InventoryPlayer inventoryPlayer, TileBlasterWorkbench tile)
	{
		super(new ContainerBlasterWorkbench(inventoryPlayer, tile));
		this.tile = tile;
	}

	@Override
	public void initGui()
	{
		this.xSize = 256;
		this.ySize = 241;

		super.initGui();
		this.buttonList.clear();

		this.buttonList.add(new PGuiButton(0, this.guiLeft + 40, this.guiTop + 6, 50, 20, I18n.format(Resources.modDot("scope"))));
		this.buttonList.add(new PGuiButton(1, this.guiLeft + 102, this.guiTop + 6, 50, 20, I18n.format(Resources.modDot("barrel"))));
		this.buttonList.add(new PGuiButton(2, this.guiLeft + 164, this.guiTop + 6, 50, 20, I18n.format(Resources.modDot("grip"))));

		this.buttonList.add(new PGuiButton(3, this.guiLeft + 40, this.guiTop + 61, 10, 20, I18n.format("◀")));
		this.buttonList.add(new PGuiButton(4, this.guiLeft + 204, this.guiTop + 61, 10, 20, I18n.format("▶")));

		GuiButton b = new PGuiButton(5, this.guiLeft + 40, this.guiTop + 115, 50, 20, I18n.format(Resources.modDot("equip")));
		b.enabled = false;
		this.buttonList.add(b);
		this.buttonList.add(new PGuiButton(5, this.guiLeft + 164, this.guiTop + 115, 50, 20, I18n.format(Resources.modDot("buy"))));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 47, this.ySize - 96 + 2, 4210752);

		String s = "Spitfire Scope";
		this.fontRendererObj.drawString(s, this.xSize / 2 - fontRendererObj.getStringWidth(s) / 2, this.guiTop + 25, GLPalette.ELECTRIC_BLUE);
		s = "1.5x zoom";
		this.fontRendererObj.drawString(s, this.xSize / 2 - fontRendererObj.getStringWidth(s) / 2, this.guiTop + 108, 4210752);
		s = "$800";
		this.fontRendererObj.drawString(s, this.xSize / 2 - fontRendererObj.getStringWidth(s) / 2, this.guiTop + 118, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}