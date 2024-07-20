package com.parzivail.mara.parsing.expression;

public class ConditionalExpression extends Expression
{
	public final Expression conditional;
	public final Expression truthy;
	public final Expression falsy;

	public ConditionalExpression(Expression conditional, Expression truthy, Expression falsy)
	{
		super(conditional.firstToken);
		this.conditional = conditional;
		this.truthy = truthy;
		this.falsy = falsy;
	}

	@Override
	public String toString()
	{
		return "(" + conditional + " ? " + truthy + " : " + falsy + ")";
	}
}
