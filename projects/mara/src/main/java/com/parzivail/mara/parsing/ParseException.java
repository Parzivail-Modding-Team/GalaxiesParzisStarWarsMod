package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.token.Token;

public class ParseException extends RuntimeException
{
	public final String message;

	public ParseException(String message, Token token)
	{
		super(String.format("['%s' @ %s] %s", token, token.location, message));
		this.message = message;
	}
}
