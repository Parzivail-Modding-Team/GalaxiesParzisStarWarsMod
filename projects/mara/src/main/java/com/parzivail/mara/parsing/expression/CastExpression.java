package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.Token;

public class CastExpression extends Expression
{
	public final Expression type;
	public final Expression value;

	public CastExpression(Token firstToken, Expression type, Expression value)
	{
		super(firstToken);
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "((" + type + ")(" + value + "))";
	}
}
