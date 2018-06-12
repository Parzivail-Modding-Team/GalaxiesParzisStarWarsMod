package com.parzivail.swg.gui;

import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class GuiScreenPlanetEnter extends GuiScreen
{
	private final NetHandlerPlayClient netHandler;
	private final int dimension;

	private int progress;

	public GuiScreenPlanetEnter(NetHandlerPlayClient netHandler, int dimension)
	{
		this.netHandler = netHandler;
		this.dimension = dimension;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
		++this.progress;

		if (this.netHandler != null)
		{
			if (this.progress % 20 == 0)
				this.netHandler.addToSendQueue(new C00PacketKeepAlive());
			this.netHandler.onNetworkTick();
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		renderPlanetEnterBackdrop(width, height, dimension, "");
	}

	public static void renderPlanetEnterBackdrop(int width, int height, int dimension, String status)
	{
		GL.PushMatrix();

		GL.Disable(EnableCap.Texture2D);

		GL.Color(GLPalette.ALMOST_BLACK);
		Fx.D2.DrawSolidRectangle(0, 0, width, height);

		GL.Enable(EnableCap.Texture2D);

		FontRenderer f = Client.mc.fontRendererObj;

		PlanetDescriptor descriptor = WorldRegister.planetDescriptorHashMap.get(dimension);

		String text = "Now approaching";
		int strW = f.getStringWidth(text);
		f.drawString(text, width / 2 - strW / 2, height / 2 - f.FONT_HEIGHT * 2, GLPalette.WHITE, false);

		f.drawString(status, 5, height - f.FONT_HEIGHT - 5, GLPalette.WHITE, false);

		int planetW = f.getStringWidth(descriptor.name);
		GL.Translate(width / 2 - planetW, height / 2 + f.FONT_HEIGHT, 0);
		GL.Scale(2);
		f.drawString(descriptor.name, 0, 0, GLPalette.WHITE, false);

		GL.PopMatrix();
	}
}
