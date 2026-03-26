package com.parzivail.toolchain.mojang.model;

/**
 * Represents a single version entry from the Mojang launcher version manifest.
 *
 * @param id              the Minecraft version identifier
 * @param type            the version type
 * @param url             the URL of the version metadata document
 * @param time            the last update time
 * @param releaseTime     the release time
 * @param sha1            the SHA-1 of the version metadata document
 * @param complianceLevel the compliance level
 */
public record MojangVersionManifestEntry(
		String id,
		String type,
		String url,
		String time,
		String releaseTime,
		String sha1,
		int complianceLevel
)
{
}
