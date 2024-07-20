package com.parzivail.mara.lexing;

public class TokenizeException extends RuntimeException
{
	public final int location;
	public final String message;

	public TokenizeException(String message, char token, int location)
	{
		super("['" + token + "' @ " + location + "] " + message);
		this.message = message;
		this.location = location;
	}

	public TokenizeException(String message, int location)
	{
		super("[@ " + location + "] " + message);
		this.message = message;
		this.location = location;
	}
}
