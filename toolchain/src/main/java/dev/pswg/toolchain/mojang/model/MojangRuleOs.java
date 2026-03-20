package dev.pswg.toolchain.mojang.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the OS-specific constraint attached to a Mojang rule.
 *
 * @param name the OS name
 * @param arch the OS architecture constraint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MojangRuleOs(
	String name,
	String arch
)
{
}
