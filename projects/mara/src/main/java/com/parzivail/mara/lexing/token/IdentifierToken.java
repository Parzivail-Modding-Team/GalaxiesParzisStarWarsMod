package com.parzivail.mara.lexing.token;

import com.parzivail.mara.lexing.TokenType;

public class IdentifierToken extends Token
{
	public final String value;

	public IdentifierToken(String value, int location)
	{
		super(TokenType.Identifier, location);
		this.value = value;
	}
}
