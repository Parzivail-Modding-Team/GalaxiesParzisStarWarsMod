package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.components.IntComponent;
import com.parzivail.pswg.components.IntComponentImpl;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

public class SwgComponents implements EntityComponentInitializer
{
	ComponentKey<IntComponent> CREDITS = ComponentRegistryV3.INSTANCE.getOrCreate(Resources.identifier("credits"), IntComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		registry.registerForPlayers(CREDITS, player -> new IntComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
	}
}
