package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.Token;

public class AstNode
{
	public final Token firstToken;

	public AstNode(Token firstToken)
	{
		this.firstToken = firstToken;
	}
}
