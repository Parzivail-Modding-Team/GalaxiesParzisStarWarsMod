package com.parzivail.toolchain.mojang.model;

/**
 * Represents a Mojang allow/disallow rule entry.
 *
 * @param action the rule action
 * @param os     the optional operating system constraint
 */
public record MojangRule(
		String action,
		MojangRuleOs os
)
{
}
