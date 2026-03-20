package dev.pswg.toolchain.mojang.model;

/**
 * Represents the OS-specific constraint attached to a Mojang rule.
 *
 * @param name the OS name
 * @param arch the OS architecture constraint
 */
public record MojangRuleOs(
	String name,
	String arch
)
{
}
