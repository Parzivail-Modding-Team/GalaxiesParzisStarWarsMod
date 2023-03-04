package com.parzivail.aurek.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class ImguiScreen extends Screen
{
	private Screen parent;

	protected ImguiScreen(Screen parent, Text title)
	{
		super(title);
		this.parent = parent;
	}

	@Override
	public void close()
	{
		client.setScreen(parent);
	}

	public abstract void process();

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		drawBackground(matrices);

		process();
	}

	protected void drawBackground(MatrixStack matrices)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF464A55, 0xFF1A283E);
	}
}
