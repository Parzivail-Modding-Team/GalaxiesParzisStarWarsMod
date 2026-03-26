package com.parzivail.toolchain.fabric;

import java.nio.file.Path;
import java.util.List;

/**
 * The resolved runtime artifacts needed for a Fabric development launch.
 *
 * @param runtimeMainClass  the Fabric runtime main class passed via {@code fabric.dli.main}
 * @param classpath         the loader-side classpath entries to prepend ahead of the game classpath
 * @param mixinJavaAgentJar the optional Mixin javaagent jar
 */
public record FabricRuntimeArtifacts(
		String runtimeMainClass,
		List<Path> classpath,
		Path mixinJavaAgentJar
)
{
}
