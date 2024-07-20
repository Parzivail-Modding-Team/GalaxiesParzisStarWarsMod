package com.parzivail.mara.lexing.token;

import com.parzivail.mara.lexing.TokenType;

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
		return type.toString() + " @ " + location;
	}
}
