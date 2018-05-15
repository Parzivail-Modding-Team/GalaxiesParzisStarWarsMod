package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class MauiCheckbox extends GuiButton
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("/textures/maui/checkbox/default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("/textures/maui/checkbox/hover.png"), 16, 23, 6, 6, 4, 10);

	public boolean checked;

	public MauiCheckbox(int buttonId, int x, int y)
	{
		super(buttonId, x, y, "");
	}

	public MauiCheckbox(int buttonId, int x, int y, int w, int h)
	{
		super(buttonId, x, y, w, h, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		this.displayString = checked ? "X" : "";
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Color(GLPalette.WHITE);
		GL.Translate(xPosition, yPosition, 0);
		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
		NinePatchResource texture = hover ? texHover : texDefault;
		texture.draw(width, height);
		float width = mc.fontRendererObj.getStringWidth(this.displayString);
		GL.Translate(Math.round((this.width - width / 2f) / 2f), Math.round((this.height - mc.fontRendererObj.FONT_HEIGHT / 2f) / 2f), 0);
		GL.Scale(0.5f);
		mc.fontRendererObj.drawString(this.displayString, 0, 0, GLPalette.ALMOST_BLACK);
		GL.PopMatrix();
		GL.PopAttrib();
	}

	public void toggle()
	{
		this.checked = !this.checked;
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}
}
