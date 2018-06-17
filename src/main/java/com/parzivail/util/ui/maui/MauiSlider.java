package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.function.Consumer;

public class MauiSlider extends GuiButton
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("textures/maui/slider/default.png"), 21, 21, 10, 10, 1, 1);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("textures/maui/slider/hover.png"), 21, 21, 10, 10, 1, 1);
	private static final NinePatchResource texActive = new NinePatchResource(Resources.location("textures/maui/slider/active.png"), 21, 21, 10, 10, 1, 1);

	private final float min;
	private final float max;
	private final String formatString;
	private float decMultiplier = 1;

	private boolean isDragging;
	private float value;

	public Consumer<MauiSlider> onChange;


	public MauiSlider(int buttonId, int x, int y, int w, int h, String buttonText, float min, float max, float value, int decimalPlaces)
	{
		super(buttonId, x, y, w, h, buttonText);
		this.min = min;
		this.max = max;
		formatString = buttonText;
		this.value = value;
		this.decMultiplier = (int)Math.pow(10, decimalPlaces);
		onChange = (dummy) -> {
		};
		this.displayString = String.format(this.formatString, value);
	}

	public MauiSlider(int buttonId, int x, int y, int w, int h, String buttonText, int min, int max, int value)
	{
		super(buttonId, x, y, w, h, buttonText);
		this.min = min;
		this.max = max;
		formatString = buttonText;
		this.value = value;
		onChange = (dummy) -> {
		};
		this.displayString = String.format(this.formatString, value);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
	{
		boolean pressed = super.mousePressed(mc, mouseX, mouseY);

		if (pressed)
			isDragging = true;

		return pressed;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY)
	{
		super.mouseReleased(mouseX, mouseY);

		if (isDragging)
		{
			boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
			if (hover)
			{
				float percent = (mouseX - xPosition) / ((float)this.width - 1);
				percent = Math.round(percent * decMultiplier) / decMultiplier;
				this.value = (max - min) * percent + min;
				this.displayString = String.format(this.formatString, this.value);
				onChange.accept(this);
			}
			isDragging = false;
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		ScaledResolution sr = Client.resolution;
		float scale = sr.getScaleFactor();
		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);

		if (isDragging && hover)
		{
			float percent = (mouseX - xPosition) / ((float)this.width - 1);
			percent = Math.round(percent * decMultiplier) / decMultiplier;
			this.value = (max - min) * percent + min;
			this.displayString = String.format(this.formatString, this.value);
		}

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Color(GLPalette.WHITE);
		GL.Translate(xPosition, yPosition, 0);
		NinePatchResource texture = hover ? texHover : texDefault;
		texture.draw(width, height);
		GL.Enable(EnableCap.ScissorTest);
		GL.Scissor(xPosition - 1, yPosition, (int)(width * (value - min) / (max - min)) + 1, height);
		texActive.draw(width, height);
		GL.EndScissor();
		float width = Maui.deJaVuSans.getWidth(this.displayString);
		GL.Scale(1f / scale);
		GL.Translate(Math.round((this.width * sr.getScaleFactor() - width) / 2f), Math.round((this.height * sr.getScaleFactor() - Maui.deJaVuSans.getHeight()) / 2f), 0);
		TextureImpl.bindNone();
		Maui.deJaVuSans.drawString(0, 0, this.displayString, org.newdawn.slick.Color.black);
		GL.PopMatrix();
		GL.PopAttrib();
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		this.value = Math.round(value / decMultiplier) * decMultiplier;
	}
}