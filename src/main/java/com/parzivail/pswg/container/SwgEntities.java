package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ChaseCamEntity;
import com.parzivail.pswg.entity.ShipEntity;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class SwgEntities
{
	public static class Ship
	{
		public static final EntityType<ChaseCamEntity> ChaseCam = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("chase_cam"), FabricEntityTypeBuilder.create(EntityCategory.MISC, ChaseCamEntity::new).size(EntityDimensions.fixed(0.1f, 0.1f)).build());

		public static final EntityType<ShipEntity> T65bXwing = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("ship"), FabricEntityTypeBuilder.create(EntityCategory.MISC, ShipEntity::new).size(EntityDimensions.fixed(1, 1)).build());
	}
}
