package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class SwgEntities
{
	public static final ArrayList<EntityType<?>> entityTypes = new ArrayList<>();

	public static class Ship
	{
		public static final EntityType<T65BXwing> T65bXwing = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("xwing_t65b"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, T65BXwing::new)
				.dimensions(EntityDimensions.fixed(1, 1))
				.trackable(128, 10, true)
				.build());

		static void register()
		{
			entityTypes.add(T65bXwing);
		}
	}

	public static class Misc
	{
		public static final EntityType<BlasterBoltEntity> BlasterBolt = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("blaster_bolt"), FabricEntityTypeBuilder
				.<BlasterBoltEntity>create(SpawnGroup.MISC, BlasterBoltEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackable(40, 5, true)
				.build());

		public static final EntityType<ThrownLightsaberEntity> ThrownLightsaber = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("thrown_lightsaber"), FabricEntityTypeBuilder
				.<ThrownLightsaberEntity>create(SpawnGroup.MISC, ThrownLightsaberEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackable(40, 5, true)
				.build());

		static void register()
		{
			entityTypes.add(BlasterBolt);
			entityTypes.add(ThrownLightsaber);
		}
	}

	public static void register()
	{
		Ship.register();
		Misc.register();
	}
}
