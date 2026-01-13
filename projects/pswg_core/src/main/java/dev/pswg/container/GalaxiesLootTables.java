package dev.pswg.container;

import dev.pswg.Galaxies;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class GalaxiesLootTables
{
	public static final RegistryKey<LootTable> IMPERIAL_CRATE = registerLootTable("container/imperial_crate");
	public static final RegistryKey<LootTable> MEDICAL_CRATE = registerLootTable("container/medical_crate");
	public static final RegistryKey<LootTable> MINING_CRATE = registerLootTable("container/mining_crate");
	public static final RegistryKey<LootTable> GENERIC_CRATE = registerLootTable("container/generic_crate");

	private static RegistryKey<LootTable> registerLootTable(String key)
	{
		return RegistryKey.of(RegistryKeys.LOOT_TABLE, Galaxies.id(key));
	}

	public static void register()
	{

	}
}
