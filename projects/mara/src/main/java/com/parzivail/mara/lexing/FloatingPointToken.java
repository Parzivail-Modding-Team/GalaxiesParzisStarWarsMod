package com.parzivail.mara.lexing;

public class FloatingPointToken extends Token
{
	public final String value;

	public FloatingPointToken(String value, int location)
	{
		super(TokenType.FloatingPointLiteral, location);
		this.value = value;
	}
}
