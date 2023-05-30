package com.parzivail.pswgdocgen;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

class DocGen
{
	private static class Context
	{
		private final PrintStream out;
		private final int baseLevel;

		private int level = 0;

		private Context(PrintStream out, int baseLevel)
		{
			this.out = out;
			this.baseLevel = baseLevel;
		}

		public void beginSection(String header)
		{
			this.level++;
			out.printf("%s %s%n%n", "#".repeat(baseLevel + level), header);
		}

		public void endSection()
		{
			this.level--;
			if (this.level < 0)
				throw new RuntimeException("Section underflow");
		}

		public void printCodeBlock(String language, String body)
		{
			out.printf("```%s%n", language);
			out.println(body);
			out.printf("```%n%n");
		}

		public void printTableHeader(String... headers)
		{
			printTableRow(headers);
			Arrays.fill(headers, "---");
			printTableRow(headers);
		}

		public void printTableRow(String... values)
		{
			out.println(String.join(" | ", values));
		}
	}

	private static class JavadocVisitor extends VoidVisitorAdapter<Context>
	{
		@Override
		public void visit(JavadocComment comment, Context arg)
		{
			super.visit(comment, arg);
			comment.getCommentedNode().ifPresent(node -> documentNode(arg, node, comment.parse()));
		}
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		var parser = new JavaParser();
		new JavadocVisitor().visit(parser.parse(new File(args[0])).getResult().orElseThrow(), new Context(System.out, 0));
	}

	private static void documentNode(Context context, Node node, Javadoc doc)
	{
		if (node instanceof ConstructorDeclaration ctor)
			documentConstructor(context, ctor, doc);
		else if (node instanceof MethodDeclaration method)
			documentMethod(context, method, doc);
	}

	private static void documentConstructor(Context context, ConstructorDeclaration node, Javadoc doc)
	{
		context.beginSection("Constructor");

		context.out.println(doc.getDescription().toText());
		context.out.println();

		context.printCodeBlock("java", node.getDeclarationAsString(true, true, true));

		context.printTableHeader("Parameter", "Description");

		for (var tag : doc.getBlockTags())
		{
			if (tag.getType() == JavadocBlockTag.Type.PARAM)
				context.printTableRow(String.format("`%s`", tag.getName().get()), tag.getContent().toText());
		}

		context.endSection();
	}

	private static void documentMethod(Context context, MethodDeclaration node, Javadoc doc)
	{
	}
}
