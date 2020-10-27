package com.parzivail.pswg.component;

import com.parzivail.pswg.Resources;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;

public class SwgEntityComponents implements EntityComponentInitializer
{
	static final ComponentKey<SwgPersistentComponents> PERSISTENT = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.identifier("persistent"), SwgPersistentComponents.class);
	static final ComponentKey<SwgVolatileComponents> VOLATILE = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.identifier("volatile"), SwgVolatileComponents.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		registry.registerForPlayers(PERSISTENT, SwgPersistentComponents::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(VOLATILE, SwgVolatileComponents::new, RespawnCopyStrategy.INVENTORY);
	}

	public static SwgPersistentComponents getPersistent(PlayerEntity player)
	{
		return PERSISTENT.get(player);
	}

	public static SwgVolatileComponents getVolatile(PlayerEntity player)
	{
		return VOLATILE.get(player);
	}
}
