package com.parzivail.mara.lexing.token;

import com.parzivail.mara.lexing.TokenType;

public class FloatingPointToken extends Token
{
	public final String value;

	public FloatingPointToken(String value, int location)
	{
		super(TokenType.FloatingPointLiteral, location);
		this.value = value;
	}
}
