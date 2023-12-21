package com.parzivail.util.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class LocalTextureButtonWidget extends ButtonWidget
{
	private final int u;
	private final int v;
	private final int textureWidth;
	private final int textureHeight;
	private final int hoveredU;
	private final int hoveredV;

	private Identifier texture = null;

	public LocalTextureButtonWidget(int x, int y, int width, int height, int u, int v, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, u, v, pressAction);
	}

	public LocalTextureButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, hoveredU, hoveredV, 256, 256, pressAction);
	}

	public LocalTextureButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction)
	{
		this(x, y, width, height, u, v, hoveredU, hoveredV, textureWidth, textureHeight, pressAction, Text.empty());
	}

	public LocalTextureButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredU, int hoveredV, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, Text text)
	{
		super(x, y, width, height, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.hoveredU = hoveredU;
		this.hoveredV = hoveredV;
	}

	public void setTexture(Identifier texture)
	{
		this.texture = texture;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
	{
		var tU = this.u;
		var tV = this.v;

		if (this.isHovered())
		{
			tU = this.hoveredU;
			tV = this.hoveredV;
		}

		RenderSystem.enableDepthTest();
		context.drawTexture(texture, this.getX(), this.getY(), tU, tV, this.width, this.height, this.textureWidth, this.textureHeight);

		//		if (this.hovered)
		//			this.renderTooltip(matrices, mouseX, mouseY);
	}
}
