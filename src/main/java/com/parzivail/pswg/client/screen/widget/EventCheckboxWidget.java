package com.parzivail.pswg.client.screen.widget;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class EventCheckboxWidget extends CheckboxWidget
{
	private final Consumer<Boolean> checkChanged;

	public EventCheckboxWidget(int x, int y, int width, int height, Text text, boolean checked, Consumer<Boolean> checkChanged)
	{
		super(x, y, width, height, text, checked);
		this.checkChanged = checkChanged;
	}

	@Override
	public void onPress()
	{
		super.onPress();
		checkChanged.accept(isChecked());
	}
}
