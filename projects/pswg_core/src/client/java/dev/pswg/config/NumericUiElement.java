package dev.pswg.config;

import java.util.Objects;

public class NumericUiElement extends UiElement
{
	private final Number value;

	public NumericUiElement(Number value)
	{
		this.value = Objects.requireNonNull(value);
	}

	public Number getValue()
	{
		return value;
	}

	@Override
	protected NumericUiElement clone()
	{
		return new NumericUiElement(value);
	}
}
