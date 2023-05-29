package com.parzivail.aurek.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		drawBackground(context);

		process();
	}

	protected void drawBackground(DrawContext context)
	{
		context.fillGradient(0, 0, this.width, this.height, 0xFF000000, 0xFF000000);
	}
}
