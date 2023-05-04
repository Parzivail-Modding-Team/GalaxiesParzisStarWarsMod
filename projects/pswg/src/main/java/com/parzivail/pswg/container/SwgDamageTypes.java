package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class SwgDamageTypes
{
	public static final RegistryKey<DamageType> BLASTER = register("blaster");

	private static RegistryKey<DamageType> register(String blaster)
	{
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Resources.id(blaster));
	}
}
