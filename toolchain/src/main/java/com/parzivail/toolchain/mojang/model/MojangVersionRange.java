package com.parzivail.toolchain.mojang.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents an optional Mojang operating system version range constraint.
 *
 * @param min the inclusive minimum version
 * @param max the inclusive maximum version
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MojangVersionRange(
		String min,
		String max
)
{
}
