package com.parzivail.util.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class LocalTextureToggleButtonWidget extends ButtonWidget
{
	private final Identifier source;
	private final int u;
	private final int v;
	private final int textureWidth;
	private final int textureHeight;
	private final int hoveredU;
	private final int hoveredV;
	private final int pressedU;
	private final int pressedV;

	private boolean pressed;

	public LocalTextureToggleButtonWidget(Identifier source, int x, int y, int width, int height, int u, int v, ButtonWidget.PressAction pressAction)
	{
		this(source, x, y, width, height, u, v, u, v, u, v, pressAction);
	}

	public LocalTextureToggleButtonWidget(Identifier source, int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, ButtonWidget.PressAction pressAction)
	{
		this(source, x, y, width, height, u, v, hoveredU, hoveredV, pressedU, pressedV, 256, 256, pressAction);
	}

	public LocalTextureToggleButtonWidget(Identifier source, int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction)
	{
		this(source, x, y, width, height, u, v, hoveredU, hoveredV, pressedU, pressedV, textureWidth, textureHeight, pressAction, Text.empty());
	}

	public LocalTextureToggleButtonWidget(Identifier source, int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, Text text)
	{
		super(x, y, width, height, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.source = source;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.hoveredU = hoveredU;
		this.hoveredV = hoveredV;
		this.pressedU = pressedU;
		this.pressedV = pressedV;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}

	public boolean isPressed()
	{
		return pressed;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
	{
		var tU = this.u;
		var tV = this.v;

		if (isPressed())
		{
			tU = this.pressedU;
			tV = this.pressedV;
		}
		else if (this.isHovered())
		{
			tU = this.hoveredU;
			tV = this.hoveredV;
		}

		RenderSystem.enableDepthTest();
		context.drawTexture(source, this.getX(), this.getY(), tU, tV, this.width, this.height, this.textureWidth, this.textureHeight);

		var oldTexture = RenderSystem.getShaderTexture(0);
		//		if (this.hovered)
		//			this.renderTooltip(matrices, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, oldTexture);
	}
}
