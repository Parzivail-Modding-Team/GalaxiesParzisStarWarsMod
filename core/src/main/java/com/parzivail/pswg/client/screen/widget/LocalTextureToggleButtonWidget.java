package com.parzivail.pswg.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value = EnvType.CLIENT)
public class LocalTextureToggleButtonWidget extends ButtonWidget
{
	private final int u;
	private final int v;
	private final int textureWidth;
	private final int textureHeight;
	private final int hoveredU;
	private final int hoveredV;
	private final int pressedU;
	private final int pressedV;

	private boolean pressed;

	public LocalTextureToggleButtonWidget(int x, int y, int width, int height, int u, int v, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, u, v, u, v, pressAction);
	}

	public LocalTextureToggleButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, hoveredU, hoveredV, pressedU, pressedV, 256, 256, pressAction);
	}

	public LocalTextureToggleButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, hoveredU, hoveredV, pressedU, pressedV, textureWidth, textureHeight, pressAction, Text.empty());
	}

	public LocalTextureToggleButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, Text text)
	{
		this(x, y, width, height, u, v, hoveredU, hoveredV, pressedU, pressedV, textureWidth, textureHeight, pressAction, EMPTY, text);
	}

	public LocalTextureToggleButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int pressedU, int pressedV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, ButtonWidget.TooltipSupplier tooltipSupplier, Text text)
	{
		super(x, y, width, height, text, pressAction, tooltipSupplier);
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
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
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
		drawTexture(matrices, this.x, this.y, tU, tV, this.width, this.height, this.textureWidth, this.textureHeight);

		var oldTexture = RenderSystem.getShaderTexture(0);
		if (this.hovered)
			this.renderTooltip(matrices, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, oldTexture);
	}
}
