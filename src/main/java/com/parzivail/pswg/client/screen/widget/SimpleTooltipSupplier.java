package com.parzivail.pswg.client.screen.widget;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.List;
import java.util.function.Supplier;

public class SimpleTooltipSupplier implements ButtonWidget.TooltipSupplier
{
	private final Screen screen;
	private final Supplier<List<? extends OrderedText>> supplier;

	public SimpleTooltipSupplier(Screen screen, Supplier<List<? extends OrderedText>> supplier)
	{
		this.screen = screen;
		this.supplier = supplier;
	}

	@Override
	public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY)
	{
		var text = supplier.get();

		if (text == null)
			return;

		screen.renderOrderedTooltip(matrices, text, mouseX, mouseY);
	}
}
