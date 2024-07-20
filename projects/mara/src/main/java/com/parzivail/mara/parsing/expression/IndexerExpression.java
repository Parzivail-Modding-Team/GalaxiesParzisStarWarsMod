package com.parzivail.mara.parsing.expression;

public class IndexerExpression extends Expression
{
	public final Expression expression;
	public final Expression indexer;

	public IndexerExpression(Expression expression, Expression indexer)
	{
		super(expression.firstToken);
		this.expression = expression;
		this.indexer = indexer;
	}

	@Override
	public String toString()
	{
		return "(" + expression + ")[(" + indexer + ")]";
	}
}
