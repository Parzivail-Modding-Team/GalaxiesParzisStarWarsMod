package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.Token;
import com.parzivail.mara.parsing.AstNode;

public class Expression extends AstNode
{
	public Expression(Token firstToken)
	{
		super(firstToken);
	}
}
