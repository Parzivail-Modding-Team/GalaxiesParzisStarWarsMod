package com.parzivail.mara.parsing.expression;

public class MemberAccessExpression extends Expression
{
	public final Expression root;
	public final Expression member;

	public MemberAccessExpression(Expression root, Expression member)
	{
		super(root.firstToken);
		this.root = root;
		this.member = member;
	}

	@Override
	public String toString()
	{
		return "(" + root + ")." + member;
	}
}
