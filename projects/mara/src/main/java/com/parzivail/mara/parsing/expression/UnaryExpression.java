package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.Token;

public class UnaryExpression extends Expression
{
	public final Token operator;
	public final Expression expression;

	public UnaryExpression(Token operator, Expression expression)
	{
		super(operator);
		this.operator = operator;
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return String.format("([%s]%s)", operator.type, expression);
	}
}
