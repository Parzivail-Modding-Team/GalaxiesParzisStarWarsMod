package com.parzivail.toolchain.template;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes XML documents using a consistent pretty-print format.
 */
public final class TemplateXmlWriter
{
	/**
	 * Prevents construction.
	 */
	private TemplateXmlWriter()
	{
	}

	/**
	 * Writes an IntelliJ XML document to disk.
	 *
	 * @param path     the output path
	 * @param document the XML document
	 *
	 * @throws IOException if the document cannot be written
	 */
	public static void write(Path path, Document document) throws IOException
	{
		var format = OutputFormat.createPrettyPrint();
		format.setIndent("  ");
		format.setNewLineAfterDeclaration(false);
		format.setEncoding(StandardCharsets.UTF_8.name());
		format.setLineSeparator("\n");

		Files.createDirectories(path.getParent());
		try (var writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8))
		{
			var xmlWriter = new XMLWriter(writer, format);
			xmlWriter.write(document);
			xmlWriter.flush();
		}
	}
}
