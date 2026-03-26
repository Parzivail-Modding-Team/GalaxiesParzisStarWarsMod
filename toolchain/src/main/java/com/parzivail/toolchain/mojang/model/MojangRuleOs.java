package com.parzivail.toolchain.mojang.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the OS-specific constraint attached to a Mojang rule.
 *
 * @param name         the OS name
 * @param arch         the OS architecture constraint
 * @param versionRange the optional operating system version range constraint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MojangRuleOs(
		String name,
		String arch,
		MojangVersionRange versionRange
)
{
}
