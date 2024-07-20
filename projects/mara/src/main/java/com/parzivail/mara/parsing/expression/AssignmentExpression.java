package com.parzivail.mara.parsing.expression;

public class AssignmentExpression extends Expression
{
	public final Expression left;
	public final Expression right;

	public AssignmentExpression(Expression left, Expression right)
	{
		super(left.firstToken);
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString()
	{
		return "(" + left + " = " + right + ")";
	}
}
