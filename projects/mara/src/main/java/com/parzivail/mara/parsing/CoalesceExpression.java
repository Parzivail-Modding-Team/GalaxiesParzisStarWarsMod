package com.parzivail.mara.parsing;

public class CoalesceExpression extends Expression
{
	public final Expression left;
	public final Expression right;

	public CoalesceExpression(Expression left, Expression right)
	{
		super(left.firstToken);
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString()
	{
		return String.format("(%s ?? %s)", left, right);
	}
}
