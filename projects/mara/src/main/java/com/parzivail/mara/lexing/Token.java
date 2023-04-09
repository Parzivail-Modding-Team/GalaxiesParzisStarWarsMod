package com.parzivail.mara.lexing;

public class Token
{
	public final TokenType type;
	public final int location;

	public Token(TokenType type, int location)
	{
		this.type = type;
		this.location = location;
	}

	@Override
	public String toString()
	{
		return String.format("%s @ %s", type.toString(), location);
	}
}
