package com.parzivail.mara.lexing;

import java.util.HashMap;

public class NumericToken extends Token
{
	public static HashMap<TokenType, Integer> BASES = new HashMap<>();

	static
	{
		BASES.put(TokenType.HexLiteral, 16);
		BASES.put(TokenType.DecimalLiteral, 10);
		BASES.put(TokenType.OctalLiteral, 8);
		BASES.put(TokenType.BinaryLiteral, 2);
	}

	public final String value;
	public final int base;

	public NumericToken(TokenType type, String value, int base, int location)
	{
		super(type, location);
		this.value = value;
		this.base = base;
	}
}
