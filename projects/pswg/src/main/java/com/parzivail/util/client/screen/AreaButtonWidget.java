package com.parzivail.util.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
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
		super(x, y, width, height, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.enabledPredicate = enabledPredicate;
	}

	@Override
	protected boolean isValidClickButton(int button)
	{
		return super.isValidClickButton(button) && enabledPredicate.test(this);
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
	{
	}
}
