package com.parzivail.toolchain.model;

import java.net.URI;

/**
 * Passive declaration of a Maven runtime dependency contributed by a module.
 *
 * @param notation   the Maven coordinate notation, optionally containing Gradle-style property placeholders
 * @param repository the repository that serves the artifact
 */
public record MavenDependencySpec(
		String notation,
		URI repository
)
{
}
