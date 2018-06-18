package com.parzivail.swg.gui;

import com.parzivail.swg.container.ContainerSabaccTable;
import com.parzivail.swg.gui.modern.ModernButton;
import com.parzivail.swg.gui.modern.ModernScrollbar;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.tile.TileSabaccTable;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.DimensionManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

public class GuiSabaccTable extends GuiContainer
{
	private TileSabaccTable tile;
	private EntityPlayer player;

	private ModernScrollbar scrollbar;

	public GuiSabaccTable(InventoryPlayer inventoryPlayer, TileSabaccTable tile)
	{
		super(new ContainerSabaccTable(inventoryPlayer, tile));
		this.tile = tile;

		// We have to use this awful hack because the EntityPlayer that's provided to
		// the Gui through the InventoryPlayer is a strictly client-based player instance
		// and isn't the real one.
		player = (EntityPlayer)DimensionManager.getWorld(inventoryPlayer.player.dimension).getEntityByID(inventoryPlayer.player.getEntityId());
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.buttonList.add(new ModernButton(0, 10, 10, 30, 10, "EQUIP"));
		this.buttonList.add(scrollbar = new ModernScrollbar(0, 10, 30, 100));

		scrollbar.setContentSize(Client.brandonReg.getHeight() * 11f / Client.resolution.getScaleFactor());
		scrollbar.setWindowSize(60);
		scrollbar.setWindowPosition(0);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Texture2D);

		GL.Color(GLPalette.WHITE);
		Fx.D2.DrawWireRectangle(30, 40, 120, 60);
		GL.Scissor(30, 40, 120, 60);

		GL.Enable(EnableCap.Texture2D);
		GL.Enable(EnableCap.Blend);

		TrueTypeFont fontrenderer = Client.brandonReg;
		int fH = fontrenderer.getHeight();

		ScaledResolution sr = Client.resolution;
		float oneOverSr = 1f / sr.getScaleFactor();

		GL.PushMatrix();
		GL.Translate(31, 41 - scrollbar.getWindowPosition(), 0);
		GL.Scale(oneOverSr);

		TextureImpl.bindNone();
		fontrenderer.drawString(0, 0, "Hello, World!", Color.white);
		fontrenderer.drawString(0, fH, "Here, we're using Brandon Grotesque", Color.white);
		fontrenderer.drawString(0, fH * 2, "Regular to simulate the font used", Color.white);
		fontrenderer.drawString(0, fH * 3, "in Battlefront 2015. Other UI styles", Color.white);
		fontrenderer.drawString(0, fH * 4, "were based on the designs of", Color.white);
		fontrenderer.drawString(0, fH * 5, "Battlefront II. This particular", Color.white);
		fontrenderer.drawString(0, fH * 6, "sample here tests a large block of", Color.white);
		fontrenderer.drawString(0, fH * 7, "text which is scissor-tested to be", Color.white);
		fontrenderer.drawString(0, fH * 8, "put behind this viewport, which is", Color.white);
		fontrenderer.drawString(0, fH * 9, "being controlled by the scrollbar on", Color.white);
		fontrenderer.drawString(0, fH * 10, "the left, which is new.", Color.white);
		GL.PopMatrix();

		GL.PopAttrib();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		ScaledResolution sr = Client.resolution;
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		GL.Color(GLPalette.ALMOST_BLACK);
		Fx.D2.DrawSolidRectangle(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
		GL.PopAttrib();
	}
}