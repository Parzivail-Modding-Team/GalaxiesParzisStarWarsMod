package com.parzivail.mara.lexing.token;

import com.parzivail.mara.lexing.TokenType;

public class KeywordToken extends Token
{
	public KeywordToken(TokenType type, int location)
	{
		super(type, location);
	}
}
