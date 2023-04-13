package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.IdentifierToken;

public class IdentifierExpression extends Expression
{
	public final IdentifierToken value;

	public IdentifierExpression(IdentifierToken value)
	{
		super(value);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.value;
	}
}
