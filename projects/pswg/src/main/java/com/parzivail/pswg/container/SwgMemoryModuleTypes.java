package com.parzivail.pswg.container;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.parzivail.pswg.Resources;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.Map;
import java.util.Optional;

public class SwgMemoryModuleTypes
{
	public static final Map<Identifier, MemoryModuleType<?>> MEMORY_MODULE_TYPES = Maps.newLinkedHashMap();

	public static final MemoryModuleType<Integer> CALL_COOLDOWN = register("call_cooldown", Codec.INT);
	public static final MemoryModuleType<Unit> FORAGE_TIME = register("forage_time", Codec.unit(Unit.INSTANCE));
	public static final MemoryModuleType<Integer> FORAGE_COOLDOWN = register("forage_cooldown", Codec.INT);

	private static <U> MemoryModuleType<U> register(String string, Codec<U> codec)
	{
		MemoryModuleType<U> memoryModuleType = new MemoryModuleType<>(Optional.of(codec));
		MEMORY_MODULE_TYPES.put(Resources.id(string), memoryModuleType);
		return memoryModuleType;
	}

	private static <U> MemoryModuleType<U> register(String string)
	{
		MemoryModuleType<U> memoryModuleType = new MemoryModuleType<>(Optional.empty());
		MEMORY_MODULE_TYPES.put(Resources.id(string), memoryModuleType);
		return memoryModuleType;
	}

	public static void register()
	{
		MEMORY_MODULE_TYPES.forEach((resourceLocation, memoryModuleType) -> Registry.register(Registries.MEMORY_MODULE_TYPE, resourceLocation, memoryModuleType));
	}

}
