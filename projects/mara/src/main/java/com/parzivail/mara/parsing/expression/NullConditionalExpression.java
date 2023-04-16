package com.parzivail.mara.parsing.expression;

public class NullConditionalExpression extends Expression
{
	public final Expression value;

	public NullConditionalExpression(Expression value)
	{
		super(value.firstToken);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return String.format("((%s)?)", value);
	}
}
