package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ChaseCamEntity;
import com.parzivail.pswg.entity.ShipEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class SwgEntities
{
	public static class Ship
	{
		public static final EntityType<ChaseCamEntity> ChaseCam = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("chase_cam"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, ChaseCamEntity::new)
				.dimensions(EntityDimensions.fixed(0.1f, 0.1f))
				.build());

		public static final EntityType<ShipEntity> T65bXwing = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("ship"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, ShipEntity::new)
				.dimensions(EntityDimensions.fixed(1, 1))
				.trackable(128, 10, true)
				.build());

		public static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static final EntityType<BlasterBoltEntity> BlasterBolt = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("blaster_bolt"), FabricEntityTypeBuilder
			.<BlasterBoltEntity>create(SpawnGroup.MISC, BlasterBoltEntity::new)
			.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
			.trackable(40, 5, true)
			.build());
}
