package com.parzivail.toolchain.mojang.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Represents a single Mojang version metadata document.
 *
 * @param id         the Minecraft version identifier
 * @param mainClass  the vanilla main class
 * @param assets     the asset identifier
 * @param assetIndex the asset index metadata
 * @param downloads  the core downloadable artifacts
 * @param libraries  the declared runtime libraries
 * @param logging    the logging metadata
 * @param arguments  the raw JVM and game argument tree
 */
public record MojangVersionMetadata(
		String id,
		String mainClass,
		String assets,
		MojangVersionMetadataAssetIndex assetIndex,
		MojangVersionMetadataDownloads downloads,
		List<MojangVersionMetadataLibrary> libraries,
		MojangVersionMetadataLogging logging,
		JsonNode arguments
)
{
}
