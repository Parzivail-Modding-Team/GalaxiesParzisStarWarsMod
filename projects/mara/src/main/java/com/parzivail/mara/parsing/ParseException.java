package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.token.Token;

public class ParseException extends RuntimeException
{
	public final String message;

	public ParseException(String message, Token token)
	{
		super("['" + token + "'] " + message);
		this.message = message;
	}
}
