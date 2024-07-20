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

			switch (token)
			{
				case NumericToken nt -> System.out.println("assertInt(tokenizer, \"" + nt.value + "\", TokenType." + nt.type + ");");
				case FloatingPointToken fpt -> System.out.println("assertFloat(tokenizer, \"" + fpt.value + "\");");
				case IdentifierToken it -> System.out.println("assertIdentifier(tokenizer, \"" + it.value + "\");");
				case StringToken st -> System.out.println("assertString(tokenizer, \"" + StringEscapeUtils.escapeJava(st.value) + "\");");
				case CharacterToken ct -> System.out.println("assertCharacter(tokenizer, '" + StringEscapeUtils.escapeJava(String.valueOf(ct.value)).replace("'", "\\'") + "');");
				default -> System.out.println("assertToken(tokenizer, TokenType." + token.type + ");");
			}
		}

		System.out.println("assertEof(tokenizer);");
	}
}
