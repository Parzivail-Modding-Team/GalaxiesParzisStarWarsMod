package dev.pswg.container;

import dev.pswg.Galaxies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class GalaxiesLootTables
{
	public static final ResourceKey<LootTable> IMPERIAL_CRATE = registerLootTable("container/imperial_crate");
	public static final ResourceKey<LootTable> MEDICAL_CRATE = registerLootTable("container/medical_crate");
	public static final ResourceKey<LootTable> MINING_CRATE = registerLootTable("container/mining_crate");
	public static final ResourceKey<LootTable> GENERIC_FOOD_CRATE = registerLootTable("container/generic_food_crate");
	public static final ResourceKey<LootTable> GENERIC_WEAPONS_CRATE = registerLootTable("container/generic_weapons_crate");
	public static final ResourceKey<LootTable> GENERIC_TECH_CRATE = registerLootTable("container/generic_tech_crate");

	private static ResourceKey<LootTable> registerLootTable(String key)
	{
		return ResourceKey.create(Registries.LOOT_TABLE, Galaxies.id(key));
	}

	public static void register()
	{

	}
}
