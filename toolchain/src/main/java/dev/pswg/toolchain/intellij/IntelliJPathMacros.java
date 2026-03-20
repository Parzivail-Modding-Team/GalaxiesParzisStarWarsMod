package dev.pswg.toolchain.intellij;

import java.nio.file.Path;

/**
 * Encodes filesystem paths into the macro-heavy URL forms that IntelliJ stores in project files.
 *
 * <p>The module-relative helpers intentionally prefer `$MODULE_DIR$` for generated module metadata.
 * IntelliJ is more stable when those `.iml` files describe their own content roots relative to the
 * file location instead of reaching back through `$PROJECT_DIR$` across nested `.idea/modules/...`
 * paths.
 */
public final class IntelliJPathMacros
{
	/**
	 * Prevents instantiation.
	 */
	private IntelliJPathMacros()
	{
	}

	/**
	 * Converts a resolved path into a `$PROJECT_DIR$` macro path when possible.
	 *
	 * @param projectRoot the PSWG project root
	 * @param path the resolved path
	 * @return the macro path
	 */
	public static String projectRelativeMacro(Path projectRoot, Path path)
	{
		Path normalizedProjectRoot = projectRoot.toAbsolutePath().normalize();
		Path normalized = path.toAbsolutePath().normalize();

		if (normalized.startsWith(normalizedProjectRoot))
		{
			return "$PROJECT_DIR$/" + normalizedProjectRoot.relativize(normalized).toString().replace('\\', '/');
		}

		return normalized.toString().replace('\\', '/');
	}

	/**
	 * Builds a `jar://...!/` URL for a resolved artifact.
	 *
	 * @param projectRoot the PSWG project root
	 * @param artifact the resolved artifact path
	 * @return the jar URL
	 */
	public static String jarUrl(Path projectRoot, Path artifact)
	{
		return "jar://" + projectRelativeMacro(projectRoot, artifact) + "!/";
	}

	/**
	 * Builds a `file://...` URL for a project-relative path.
	 *
	 * @param projectRoot the PSWG project root
	 * @param path the target path
	 * @return the file URL
	 */
	public static String fileUrl(Path projectRoot, Path path)
	{
		return "file://" + projectRelativeMacro(projectRoot, path);
	}

	/**
	 * Builds a module-local `file://...` URL for a path rooted under the module directory.
	 *
	 * @param moduleRoot the module root path
	 * @param path the target path
	 * @return the file URL
	 */
	public static String moduleFileUrl(Path moduleRoot, Path path)
	{
		Path normalizedModuleRoot = moduleRoot.toAbsolutePath().normalize();
		Path normalizedPath = path.toAbsolutePath().normalize();

		if (normalizedPath.startsWith(normalizedModuleRoot))
		{
			Path relativePath = normalizedModuleRoot.relativize(normalizedPath);

			if (relativePath.toString().isEmpty())
			{
				return "file://$MODULE_DIR$/../../../../projects/" + normalizedModuleRoot.getFileName();
			}

			return "file://$MODULE_DIR$/../../../../projects/"
				+ normalizedModuleRoot.getFileName()
				+ "/"
				+ relativePath.toString().replace('\\', '/');
		}

		return "file://" + normalizedPath.toString().replace('\\', '/');
	}
}
