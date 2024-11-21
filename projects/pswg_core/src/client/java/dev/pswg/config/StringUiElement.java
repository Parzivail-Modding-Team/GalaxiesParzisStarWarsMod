package dev.pswg.config;

public class StringUiElement extends UiElement
{
	private final String value;

	public StringUiElement(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	protected StringUiElement clone()
	{
		return new StringUiElement(value);
	}
}
