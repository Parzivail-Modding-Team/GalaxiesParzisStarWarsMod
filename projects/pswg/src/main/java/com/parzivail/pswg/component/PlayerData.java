package com.parzivail.pswg.component;

import com.parzivail.pswg.Resources;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerData implements EntityComponentInitializer
{
	static final ComponentKey<PersistentPublicPlayerData> PERSISTENT_PUBLIC = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.id("per_pub"), PersistentPublicPlayerData.class);
	static final ComponentKey<VolatilePublicPlayerData> VOLATILE_PUBLIC = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.id("vol_pub"), VolatilePublicPlayerData.class);
	static final ComponentKey<VolatilePrivatePlayerData> VOLATILE_PRIVATE = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.id("vol_prv"), VolatilePrivatePlayerData.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		registry.registerForPlayers(PERSISTENT_PUBLIC, PersistentPublicPlayerData::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(VOLATILE_PUBLIC, VolatilePublicPlayerData::new, RespawnCopyStrategy.INVENTORY);
		registry.registerForPlayers(VOLATILE_PRIVATE, VolatilePrivatePlayerData::new, RespawnCopyStrategy.INVENTORY);
	}

	public static PersistentPublicPlayerData getPersistentPublic(PlayerEntity player)
	{
		return PERSISTENT_PUBLIC.get(player);
	}

	public static VolatilePublicPlayerData getVolatilePublic(PlayerEntity player)
	{
		return VOLATILE_PUBLIC.get(player);
	}

	public static VolatilePrivatePlayerData getVolatilePrivate(PlayerEntity player)
	{
		return VOLATILE_PRIVATE.get(player);
	}
}
