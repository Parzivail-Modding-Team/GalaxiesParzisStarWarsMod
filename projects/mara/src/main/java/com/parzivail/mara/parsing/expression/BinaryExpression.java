package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.Token;

public class BinaryExpression extends Expression
{
	public final Expression left;
	public final Token operator;
	public final Expression right;

	public BinaryExpression(Expression left, Token operator, Expression right)
	{
		super(left.firstToken);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public String toString()
	{
		return "(" + left + " [" + operator.type + "] " + right + ")";
	}
}
