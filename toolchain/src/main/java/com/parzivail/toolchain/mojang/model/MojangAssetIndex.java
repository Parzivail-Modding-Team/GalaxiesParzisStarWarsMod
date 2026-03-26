package com.parzivail.toolchain.mojang.model;

import java.util.Map;

/**
 * Represents the Mojang asset index document for a version.
 *
 * @param objects the addressable asset objects by logical asset path
 */
public record MojangAssetIndex(
		Map<String, MojangAssetObject> objects
)
{
}
