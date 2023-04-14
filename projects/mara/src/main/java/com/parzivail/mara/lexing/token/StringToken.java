package com.parzivail.mara.lexing.token;

import com.parzivail.mara.lexing.TokenType;

public class StringToken extends Token
{
	public final String value;

	public StringToken(String value, int location)
	{
		super(TokenType.StringLiteral, location);
		this.value = value;
	}
}
