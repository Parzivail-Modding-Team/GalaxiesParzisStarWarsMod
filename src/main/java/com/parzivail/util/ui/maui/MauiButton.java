package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

public class MauiButton extends GuiButton
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("/textures/maui/button/default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("/textures/maui/button/hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texActive = new NinePatchResource(Resources.location("/textures/maui/button/active.png"), 16, 23, 6, 6, 4, 10);

	public MauiButton(int buttonId, int x, int y, String buttonText)
	{
		super(buttonId, x, y, buttonText);
	}

	public MauiButton(int buttonId, int x, int y, int w, int h, String buttonText)
	{
		super(buttonId, x, y, w, h, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Color(GLPalette.WHITE);
		GL.Translate(xPosition, yPosition, 0);
		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
		boolean active = hover && Mouse.isButtonDown(0);
		NinePatchResource texture = active ? texActive : hover ? texHover : texDefault;
		texture.draw(width, height);
		float width = mc.fontRendererObj.getStringWidth(this.displayString);
		GL.Translate(Math.round((this.width - width / 2f) / 2f), Math.round((this.height - mc.fontRendererObj.FONT_HEIGHT / 2f) / 2f), 0);
		GL.Scale(0.5f);
		mc.fontRendererObj.drawString(this.displayString, 0, 0, active ? GLPalette.WHITE : GLPalette.ALMOST_BLACK);
		GL.PopMatrix();
		GL.PopAttrib();
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}
}
