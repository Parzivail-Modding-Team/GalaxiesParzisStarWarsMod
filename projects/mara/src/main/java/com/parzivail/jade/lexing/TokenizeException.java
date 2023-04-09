package com.parzivail.jade.lexing;

public class TokenizeException extends Exception
{
	public final int location;
	public final String message;

	public TokenizeException(String message, char token, int location)
	{
		super(String.format("['%s' @ %s] %s", token, location, message));
		this.message = message;
		this.location = location;
	}

	public TokenizeException(String message, int location)
	{
		super(String.format("[@ %s] %s", location, message));
		this.message = message;
		this.location = location;
	}
}
