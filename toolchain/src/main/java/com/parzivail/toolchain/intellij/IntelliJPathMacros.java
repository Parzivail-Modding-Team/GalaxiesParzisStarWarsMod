package com.parzivail.toolchain.intellij;

import com.parzivail.toolchain.path.ToolchainPaths;

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
	 * @param path the resolved path
	 *
	 * @return the macro path
	 */
	public static String projectRelativeMacro(Path path)
	{
		var normalizedProjectRoot = ToolchainPaths.PROJECT_ROOT.toAbsolutePath().normalize();
		var normalized = path.toAbsolutePath().normalize();

		if (normalized.startsWith(normalizedProjectRoot))
		{
			return "$PROJECT_DIR$/" + normalizedProjectRoot.relativize(normalized).toString().replace('\\', '/');
		}

		return normalized.toString().replace('\\', '/');
	}

	/**
	 * Builds a `jar://...!/` URL for a resolved artifact.
	 *
	 * @param artifact the resolved artifact path
	 *
	 * @return the jar URL
	 */
	public static String jarUrl(Path artifact)
	{
		return "jar://" + projectRelativeMacro(artifact) + "!/";
	}

	/**
	 * Builds a `file://...` URL for a project-relative path.
	 *
	 * @param path the target path
	 *
	 * @return the file URL
	 */
	public static String fileUrl(Path path)
	{
		return "file://" + projectRelativeMacro(path);
	}

	/**
	 * Builds a module-local `file://...` URL for a path rooted under the module directory.
	 *
	 * @param moduleRoot the module root path
	 * @param path       the target path
	 *
	 * @return the file URL
	 */
	public static String moduleFileUrl(Path moduleRoot, Path path)
	{
		var normalizedModuleRoot = moduleRoot.toAbsolutePath().normalize();
		var normalizedPath = path.toAbsolutePath().normalize();

		if (normalizedPath.startsWith(normalizedModuleRoot))
		{
			var relativePath = normalizedModuleRoot.relativize(normalizedPath);

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

	/**
	 * Builds a module-local `file://...` URL for the standalone toolchain module registered under the
	 * root project.
	 *
	 * @param toolchainRoot the standalone toolchain root
	 * @param path          the target path
	 *
	 * @return the file URL
	 */
	public static String toolchainModuleFileUrl(Path toolchainRoot, Path path)
	{
		var normalizedToolchainRoot = toolchainRoot.toAbsolutePath().normalize();
		var normalizedPath = path.toAbsolutePath().normalize();

		if (normalizedPath.startsWith(normalizedToolchainRoot))
		{
			var relativePath = normalizedToolchainRoot.relativize(normalizedPath);

			if (relativePath.toString().isEmpty())
			{
				return "file://$MODULE_DIR$/../../../../toolchain";
			}

			return "file://$MODULE_DIR$/../../../../toolchain/"
			       + relativePath.toString().replace('\\', '/');
		}

		return "file://" + normalizedPath.toString().replace('\\', '/');
	}

	/**
	 * Builds the module-relative output URL used by generated `.iml` files under
	 * `.idea/modules/projects/...`.
	 *
	 * @param outputName the IntelliJ output directory name
	 *
	 * @return the file URL
	 */
	public static String generatedModuleOutputUrl(String outputName)
	{
		return "file://$MODULE_DIR$/../../../../out/production/" + outputName;
	}
}
