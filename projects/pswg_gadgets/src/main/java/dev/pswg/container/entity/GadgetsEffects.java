package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.effects.IntoxicatedEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class GadgetsEffects
{
	public static final RegistryEntry<StatusEffect> INTOXICATED = Registry.registerReference(Registries.STATUS_EFFECT, Gadgets.id("intoxicated"), new IntoxicatedEffect());

	public static void register()
	{

	}
}
