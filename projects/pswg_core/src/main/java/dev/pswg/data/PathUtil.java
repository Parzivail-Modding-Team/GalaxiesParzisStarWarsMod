package dev.pswg.data;

/**
 * Utilities for working with paths
 */
public final class PathUtil
{
	/**
	 * Makes a path relative to a given root
	 *
	 * @param path The path to make relative
	 * @param root The root to make the path relative to
	 *
	 * @return The relative path
	 */
	public static String makeRelative(String path, String root)
	{
		if (!root.endsWith("/"))
			root += "/";

		if (!path.startsWith(root))
			throw new IllegalArgumentException("Path '%s' does not start with root '%s'".formatted(path, root));

		return path.substring(root.length());
	}
}
