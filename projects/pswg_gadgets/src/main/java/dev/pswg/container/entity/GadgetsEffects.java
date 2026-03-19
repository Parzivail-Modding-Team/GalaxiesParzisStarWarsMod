package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.effects.IntoxicatedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class GadgetsEffects
{
	public static final Holder<MobEffect> INTOXICATED = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Gadgets.id("intoxicated"), new IntoxicatedEffect());

	public static void register()
	{

	}
}
