package dev.pswg.config;

public class CheckboxUiElement extends UiElement
{
	private final boolean value;

	public CheckboxUiElement(boolean value)
	{
		this.value = value;
	}

	public boolean getValue()
	{
		return value;
	}

	@Override
	protected CheckboxUiElement clone()
	{
		return new CheckboxUiElement(value);
	}
}
