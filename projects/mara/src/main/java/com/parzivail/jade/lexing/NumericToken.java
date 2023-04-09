package com.parzivail.jade.lexing;

public class NumericToken extends Token
{
	public final String value;

	public NumericToken(TokenType type, String value, int location)
	{
		super(type, location);
		this.value = value;
	}
}
