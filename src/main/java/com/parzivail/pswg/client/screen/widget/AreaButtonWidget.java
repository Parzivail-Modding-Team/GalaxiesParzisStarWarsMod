package com.parzivail.pswg.client.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(value = EnvType.CLIENT)
public class AreaButtonWidget extends ButtonWidget
{
	public AreaButtonWidget(int x, int y, int width, int height, PressAction pressAction)
	{
		this(x, y, width, height, pressAction, LiteralText.EMPTY);
	}

	public AreaButtonWidget(int x, int y, int width, int height, PressAction pressAction, Text text)
	{
		this(x, y, width, height, pressAction, EMPTY, text);
	}

	public AreaButtonWidget(int x, int y, int width, int height, PressAction pressAction, TooltipSupplier tooltipSupplier, Text text)
	{
		super(x, y, width, height, text, pressAction, tooltipSupplier);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
	}
}
