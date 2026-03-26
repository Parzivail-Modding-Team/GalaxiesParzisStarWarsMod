package com.parzivail.toolchain.template;

import java.nio.file.Path;

/**
 * Shared XML escaping helpers for generated IntelliJ and log4j metadata.
 */
public final class XmlEscaper
{
	/**
	 * Prevents construction.
	 */
	private XmlEscaper()
	{
	}

	/**
	 * Escapes a raw XML attribute value.
	 *
	 * @param value the raw value
	 *
	 * @return the escaped value
	 */
	public static String escapeAttribute(String value)
	{
		return value.replace("&", "&amp;")
		            .replace("\"", "&quot;")
		            .replace("<", "&lt;")
		            .replace(">", "&gt;");
	}

	/**
	 * Escapes a filesystem path for XML attribute use.
	 *
	 * @param path the path to escape
	 *
	 * @return the escaped path value
	 */
	public static String escapePath(Path path)
	{
		return escapeAttribute(path.toAbsolutePath().toString().replace('\\', '/'));
	}
}
