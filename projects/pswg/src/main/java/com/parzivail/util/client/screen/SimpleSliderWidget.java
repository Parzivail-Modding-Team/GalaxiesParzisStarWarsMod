package com.parzivail.util.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class SimpleSliderWidget extends ButtonWidget
{
	private final int trackU;
	private final int trackV;
	private final int thumbU;
	private final int thumbV;
	private final int thumbHoverU;
	private final int thumbHoverV;
	private final int thumbWidth;
	private final int thumbHeight;
	private final int textureWidth;
	private final int textureHeight;
	private final Consumer<SimpleSliderWidget> onChange;

	private Identifier texture = null;
	private boolean dirty;
	private float value;

	public SimpleSliderWidget(int x, int y, int width, int height, int trackU, int trackV, int thumbU, int thumbV, int thumbHoverU, int thumbHoverV, int thumbWidth, int thumbHeight, int textureWidth, int textureHeight, Consumer<SimpleSliderWidget> onChange)
	{
		super(x, y, width, height, Text.of(""), button -> {
		}, EMPTY_TOOLTIP, DEFAULT_NARRATION_SUPPLIER);
		this.trackU = trackU;
		this.trackV = trackV;
		this.thumbU = thumbU;
		this.thumbV = thumbV;
		this.thumbHoverU = thumbHoverU;
		this.thumbHoverV = thumbHoverV;
		this.thumbWidth = thumbWidth;
		this.thumbHeight = thumbHeight;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.onChange = onChange;
	}

	public void setTexture(Identifier texture)
	{
		this.texture = texture;
	}

	public float getValue()
	{
		return value;
	}

	private void setValueFromMouseX(double mouseX)
	{
		this.value = MathHelper.clamp((float)((mouseX - this.getX()) / (this.width - 2)), 0, 1);
	}

	public void setValue(float v)
	{
		this.value = v;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && clientAreaContains(mouseX, mouseY))
		{
			setValueFromMouseX(mouseX);
			dirty = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private boolean clientAreaContains(double x, double y)
	{
		return x >= this.getX() && x <= this.getX() + width && y >= this.getY() && y <= this.getY() + height;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && dirty)
		{
			setValueFromMouseX(mouseX);
			dirty = true;
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && dirty)
		{
			commit();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		var oldTexture = RenderSystem.getShaderTexture(0);

		if (texture != null)
			RenderSystem.setShaderTexture(0, texture);

		var tU = this.thumbU;
		var tV = this.thumbV;

		if (this.isHovered() || dirty)
		{
			tU = this.thumbHoverU;
			tV = this.thumbHoverV;
		}

		RenderSystem.enableDepthTest();

		drawTexture(matrices, this.getX(), this.getY(), this.trackU, this.trackV, this.width, this.height, this.textureWidth, this.textureHeight);

		drawTexture(matrices, this.getX() + 1 + Math.round(value * (width - 2)) - this.thumbWidth / 2, this.getY() - (this.thumbHeight - this.height) / 2, tU, tV, this.thumbWidth, this.thumbHeight, this.textureWidth, this.textureHeight);

		if (this.isHovered())
			this.renderTooltip(matrices, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, oldTexture);
	}

	public void commit()
	{
		if (!dirty)
			return;

		onChange.accept(this);
		dirty = false;
	}
}
