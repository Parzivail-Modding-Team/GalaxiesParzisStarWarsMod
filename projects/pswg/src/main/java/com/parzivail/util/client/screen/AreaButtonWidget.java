package com.parzivail.util.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Predicate;

@Environment(value = EnvType.CLIENT)
public class AreaButtonWidget extends ButtonWidget
{
	private final Predicate<ButtonWidget> enabledPredicate;

	public AreaButtonWidget(int x, int y, int width, int height, Predicate<ButtonWidget> enabledPredicate, PressAction pressAction)
	{
		this(x, y, width, height, enabledPredicate, pressAction, Text.empty());
	}

	public AreaButtonWidget(int x, int y, int width, int height, Predicate<ButtonWidget> enabledPredicate, PressAction pressAction, Text text)
	{
		this(x, y, width, height, enabledPredicate, pressAction, EMPTY, text);
	}

	public AreaButtonWidget(int x, int y, int width, int height, Predicate<ButtonWidget> enabledPredicate, PressAction pressAction, TooltipSupplier tooltipSupplier, Text text)
	{
		super(x, y, width, height, text, pressAction, tooltipSupplier);
		this.enabledPredicate = enabledPredicate;
	}

	@Override
	protected boolean isValidClickButton(int button)
	{
		return super.isValidClickButton(button) && enabledPredicate.test(this);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
	}
}
