package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import java.awt.*;
import java.io.InputStream;

public class MauiButton extends GuiButton
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("/textures/maui/button/default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("/textures/maui/button/hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texActive = new NinePatchResource(Resources.location("/textures/maui/button/active.png"), 16, 23, 6, 6, 4, 10);

	static ResourceLocation blenderFontResource = Resources.location("font/DejaVuSans.ttf");
	static TrueTypeFont blenderFont;

	static
	{
	}

	public MauiButton(int buttonId, int x, int y, String buttonText)
	{
		super(buttonId, x, y, buttonText);
	}

	public MauiButton(int buttonId, int x, int y, int w, int h, String buttonText)
	{
		super(buttonId, x, y, w, h, buttonText);
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(blenderFontResource);
			InputStream inputStream = res.getInputStream();

			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(11f); // set font size
			blenderFont = new TrueTypeFont(awtFont2, true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Color(GLPalette.WHITE);
		GL.Translate(xPosition, yPosition, 0);
		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
		boolean active = hover && Mouse.isButtonDown(0);
		NinePatchResource texture = active ? texActive : hover ? texHover : texDefault;
		texture.draw(width, height);
		float width = blenderFont.getWidth(this.displayString);
		GL.Scale(1f / sr.getScaleFactor());
		GL.Translate(Math.round((this.width * sr.getScaleFactor() - width) / 2f), Math.round((this.height * sr.getScaleFactor() - blenderFont.getHeight()) / 2f), 0);
		TextureImpl.bindNone();
		blenderFont.drawString(0, 0, this.displayString, org.newdawn.slick.Color.black);
		GL.PopMatrix();
		GL.PopAttrib();
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}
}
