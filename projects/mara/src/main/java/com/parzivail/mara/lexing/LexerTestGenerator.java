package com.parzivail.mara.lexing;

import com.parzivail.mara.lexing.token.*;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LexerTestGenerator
{
	public static void main(String[] args) throws IOException
	{
		var str = Files.readString(new File(args[0]).toPath());
		var t = new Tokenizer(str);

		while (t.consume())
		{
			var tokens = t.getTokens();
			var token = tokens.getLast();

			if (token instanceof NumericToken nt)
				System.out.printf("assertInt(tokenizer, \"%s\", TokenType.%s);%n", nt.value, nt.type);
			else if (token instanceof FloatingPointToken fpt)
				System.out.printf("assertFloat(tokenizer, \"%s\");%n", fpt.value);
			else if (token instanceof IdentifierToken it)
				System.out.printf("assertIdentifier(tokenizer, \"%s\");%n", it.value);
			else if (token instanceof StringToken st)
				System.out.printf("assertString(tokenizer, \"%s\");%n", StringEscapeUtils.escapeJava(st.value));
			else if (token instanceof CharacterToken ct)
				System.out.printf("assertCharacter(tokenizer, '%s');%n", StringEscapeUtils.escapeJava(String.valueOf(ct.value)).replace("'", "\\'"));
			else
				System.out.printf("assertToken(tokenizer, TokenType.%s);%n", token.type);
		}

		System.out.println("assertEof(tokenizer);");
	}
}
