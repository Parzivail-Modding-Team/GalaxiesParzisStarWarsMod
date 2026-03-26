package com.parzivail.toolchain.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Renders classpath-backed file templates using simple token replacement.
 */
public final class FileTemplateRenderer
{
	/**
	 * Prevents construction.
	 */
	private FileTemplateRenderer()
	{
	}

	/**
	 * Renders a classpath template with the provided replacement values.
	 *
	 * @param resourcePath the classpath resource path
	 * @param values       the replacement values keyed by token name
	 *
	 * @return the rendered template content
	 *
	 * @throws IOException if the template cannot be read
	 */
	public static String render(String resourcePath, Map<String, String> values) throws IOException
	{
		var template = loadTemplate(resourcePath);
		var rendered = template;

		for (var entry : values.entrySet())
		{
			rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
		}

		return rendered;
	}

	/**
	 * Loads a classpath template resource as a UTF-8 string.
	 *
	 * @param resourcePath the classpath resource path
	 *
	 * @return the loaded template text
	 *
	 * @throws IOException if the resource does not exist or cannot be read
	 */
	private static String loadTemplate(String resourcePath) throws IOException
	{
		try (var inputStream = FileTemplateRenderer.class.getClassLoader().getResourceAsStream(resourcePath))
		{
			if (inputStream == null)
			{
				throw new IOException("Missing file template resource: " + resourcePath);
			}

			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
