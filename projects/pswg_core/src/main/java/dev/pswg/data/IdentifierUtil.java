package dev.pswg.data;

import net.minecraft.resources.Identifier;

/**
 * A set of utilities for working with identifiers
 */
public final class IdentifierUtil
{
	/**
	 * Determines if the given identifier has the specified extension.
	 *
	 * @param identifier The identifier to check
	 * @param extension  The extension to check for, without the leading '.'
	 *
	 * @return true if the identifier's path ends with the specified extension, false otherwise.
	 */
	public static boolean hasExtension(Identifier identifier, String extension)
	{
		return identifier.getPath().endsWith("." + extension);
	}

	/**
	 * Checks if the given identifier represents a JSON file.
	 *
	 * @param identifier The identifier to check
	 *
	 * @return true if the identifier's path ends with '.json', false otherwise
	 */
	public static boolean isJsonFile(Identifier identifier)
	{
		return hasExtension(identifier, "json");
	}
}
