package com.parzivail.mara.lexing;

public class NumericToken extends Token
{
	public final String value;

	public NumericToken(TokenType type, String value, int location)
	{
		super(type, location);
		this.value = value;
	}

	public int uintValue()
	{
		return Integer.parseUnsignedInt(value);
	}
}
