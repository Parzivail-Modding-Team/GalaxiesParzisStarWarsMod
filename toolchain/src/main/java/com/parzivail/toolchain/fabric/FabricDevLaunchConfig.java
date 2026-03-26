package com.parzivail.toolchain.fabric;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Parsed representation of a Loom-style dev launch configuration file.
 *
 * @param path     the source configuration file path
 * @param sections the ordered launch configuration sections and values
 */
public record FabricDevLaunchConfig(
		Path path,
		Map<String, List<String>> sections
)
{
}
