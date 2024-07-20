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
import java.util.List;
import java.util.stream.Collectors;

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
			out.println("#".repeat(baseLevel + level) + " " + header);
			out.println();
		}

		public void endSection()
		{
			this.level--;
			if (this.level < 0)
				throw new RuntimeException("Section underflow");
			out.println();
		}

		public void printCodeBlock(String language, String body)
		{
			out.println("```" + language);
			out.println(body);
			out.println("```");
			out.println();
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
		new JavadocVisitor().visit(parser.parse(new File(args[0])).getResult().orElseThrow(), new Context(System.out, 1));
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

		context.out.println(trimCommentWrappedLines(doc.getDescription().toText()));
		context.out.println();

		context.printCodeBlock("java", node.getDeclarationAsString(true, true, true));

		printParams(context, doc);

		context.endSection();
	}

	private static void documentMethod(Context context, MethodDeclaration node, Javadoc doc)
	{
		if (node.isAnnotationPresent("Internal"))
			return;

		context.beginSection("Method: `" + node.getName() + "`");

		context.out.println(trimCommentWrappedLines(doc.getDescription().toText()));
		context.out.println();

		context.printCodeBlock("java", node.getDeclarationAsString(true, true, true));

		printParams(context, doc);

		context.out.println();

		if (!node.getType().isVoidType())
		{
			context.beginSection("Returns");

			for (var tag : doc.getBlockTags())
			{
				if (tag.getType() == JavadocBlockTag.Type.RETURN)
					context.out.println("* " + tag.getContent().toText());
			}

			context.endSection();
		}

		context.endSection();
	}

	private static void printParams(Context context, Javadoc doc)
	{
		var params = getJavadocParams(doc);
		if (!params.isEmpty())
			context.printTableHeader("Parameter", "Description");

		for (var tag : params)
			context.printTableRow("`" + tag.getName().get() + "`", trimCommentWrappedLines(tag.getContent().toText()));
	}

	private static List<JavadocBlockTag> getJavadocParams(Javadoc doc)
	{
		return doc.getBlockTags().stream().filter(t -> t.getType() == JavadocBlockTag.Type.PARAM).toList();
	}

	private static String trimCommentWrappedLines(String str)
	{
		return Arrays.stream(str.split("[\r\n]+")).map(String::strip).collect(Collectors.joining(" "));
	}
}
