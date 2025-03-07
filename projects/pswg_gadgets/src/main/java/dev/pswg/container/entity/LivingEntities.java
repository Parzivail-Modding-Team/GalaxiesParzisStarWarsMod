package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.living.BanthaEntity;
import dev.pswg.registry.Registrar;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class LivingEntities
{

	public static final EntityType<BanthaEntity> Bantha = Registrar.entityType(Gadgets.id("bantha"), EntityType.Builder
			.create(BanthaEntity::new, SpawnGroup.CREATURE)
			.dimensions(2.0f, 2.9f)
	);

	public static void register()
	{
		FabricDefaultAttributeRegistry.register(Bantha, BanthaEntity.createAttributes());
	}
}
