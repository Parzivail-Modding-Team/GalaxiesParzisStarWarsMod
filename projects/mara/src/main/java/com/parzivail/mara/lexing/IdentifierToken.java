package com.parzivail.mara.lexing;

public class IdentifierToken extends Token
{
	public final String value;

	public IdentifierToken(String value, int location)
	{
		super(TokenType.Identifier, location);
		this.value = value;
	}
}
